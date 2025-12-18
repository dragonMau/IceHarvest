package curome.world.blocks;

import arc.Core;
import arc.struct.ObjectMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import curome.Logging;
import curome.world.type.CuromeThermalRecipe;
import mindustry.content.Liquids;
import mindustry.logic.LAccess;
import mindustry.type.ItemStack;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Tile;
import mindustry.ui.Bar;

import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Attribute;

import arc.struct.Seq;
import arc.math.Mathf;


import static mindustry.Vars.*;

public class CuromeIcebox extends GenericCrafter {
    public boolean displayEfficiency = true;
    public Seq<CuromeThermalRecipe> recipes = new Seq<>();
    public Seq<ItemStack> outputItemsBuilder = new Seq<>();
    public Seq<LiquidStack> outputLiquidsBuilder = new Seq<>();
    public float heatCapacityBase = 50000f;
    public ObjectMap<Item, Float> heatCapacityItems = new ObjectMap<>();
    public ObjectMap<Liquid, Float> heatCapacityLiquids = new ObjectMap<>();

    public float conductivity = 4000f;
    public final float ambientTemperature = 25f;
    public final float ambientHeatTransition = 200f;
    public final float[] temperatureLimits = new float[]{-273.15f, 1.416784e32f};
    public final float tolerance = 0.1f;

    public CuromeIcebox(String name){
        super(name);
    }

    public void addRecipe(CuromeThermalRecipe recipe) {
        recipes.add(recipe);
        consumeLiquid(recipe.liquid.liquid, 0f);
        consumeItem(recipe.solid.item, 1);
        outputItemsBuilder.add(recipe.solid);
        heatCapacityItems.put(recipe.solid.item, recipe.heatCapacitySolid);
        outputLiquidsBuilder.add(recipe.liquid);
        heatCapacityLiquids.put(recipe.liquid.liquid, recipe.heatCapacityLiquid);
    }

    @Override
    public void init() {
        outputItems = outputItemsBuilder.toArray(ItemStack.class);
        outputLiquids = outputLiquidsBuilder.toArray(LiquidStack.class);
        super.init();
    }

    @Override
    public void setBars(){
        super.setBars();
        for(var barName : barMap.keys()){
            if (barName.contains("liquid"))
                removeBar(barName);
        }
        // new bar for heat
        addBar("heat", (CuromeIceboxBuild entity) -> new Bar(
            () -> Core.bundle.format("bar.temperature", Mathf.round(entity.energy/entity.heatCapacity)),
            () -> entity.latentEnergy < 0 ? Liquids.cryofluid.color : Liquids.slag.color,
                entity::progress
        ));

        for(var stack : outputLiquids){
            addLiquidBar(stack.liquid);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!displayEfficiency) return;

        drawPlaceText(
                Core.bundle.format(
                        "bar.temperature",
                        Mathf.round(blockTemperature(x, y))
                ),
                x, y, valid
        );
    }

    public float heatToTemp(float heat) {
        return ambientTemperature + ambientHeatTransition * heat;
    }
    public static class TempBlocks {
        float total = 0f;
        float count = 0;
        public void add(TempBlocks other) {
            total += other.total;
            count += other.count;
        }
        public float avg() {
            return count!=0 ? total/count:0f;
        }
    }
    public TempBlocks floorTemperature(int x, int y) {
        Tile tile = world.tile(x, y);
        TempBlocks tempBlocks = new TempBlocks();
        if(tile == null) return tempBlocks;
        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(floating || !other.floor().isDeep()){
                tempBlocks.total += heatToTemp(other.floor().attributes.get(Attribute.heat));
                tempBlocks.count++;
            }
        }
        return tempBlocks;
    }
    public TempBlocks wallTemperature(int tx, int ty) {
        TempBlocks tempBlocks = new TempBlocks();
        int cornerX = tx - (size-1)/2, cornerY = ty - (size-1)/2, s = size;

        for(int i = 0; i < size; i++) {
            int rx = 0, ry = 0;

            for (int j = 0; j < 4; j++) {
                switch (j) {
                    case 0 -> {
                        rx = cornerX + s;
                        ry = cornerY + i;
                    }
                    case 1 -> {
                        rx = cornerX + i;
                        ry = cornerY + s;
                    }
                    case 2 -> {
                        rx = cornerX - 1;
                        ry = cornerY + i;
                    }
                    case 3 -> {
                        rx = cornerX + i;
                        ry = cornerY - 1;
                    }
                }

                Tile other = world.tile(rx, ry);
                if (other != null) {
                    if (!other.solid()) { // air
                        tempBlocks.total += ambientTemperature;
                        tempBlocks.count ++;
                    }
                    float heat = other.block().attributes.get(Attribute.heat);
                    if (heat != 0) { // cold/warm wall
                        tempBlocks.total += heatToTemp(heat);
                        tempBlocks.count ++;
                    }
                    // neutral wall
                    tempBlocks.total += ambientTemperature/4f;
                    tempBlocks.count += 1/4f;
                }
            }
        }
        return tempBlocks;
    }
    public float blockTemperature(int x, int y) {
        TempBlocks tempBlocks = new TempBlocks();
        tempBlocks.add(floorTemperature(x, y));
        tempBlocks.add(wallTemperature(x, y));
        return tempBlocks.avg();
    }

    public class CuromeIceboxBuild extends GenericCrafterBuild {
        public float envTemperature;
        public float energy = heatCapacityBase * ambientTemperature;
        public float heatCapacity = heatCapacityBase;
        public float temperature = 0f;
        public float latentEnergy = 0f;
        public float recipeLatent = 0f;

        public void updateHeatCapacity() {
            heatCapacity = heatCapacityBase;
            for(var hCap : heatCapacityItems) {
                heatCapacity += items.get(hCap.key) * hCap.value;
            }
            for(var hCap : heatCapacityLiquids) {
                heatCapacity += liquids.get(hCap.key) * hCap.value;
            }
        }
        public void updateTemperature() {
            // if this has solid, cant be > minimal recipe temperature
            // if this has liquid, cant be < maximal recipe temperature
            float minTemp = temperatureLimits[1];
            float maxTemp = temperatureLimits[0];
            for(var recipe : recipes) {
                // if this has ice
                if (items.get(recipe.solid.item) >= recipe.solid.amount) {
                    // this can't get hotter than recipe.temperature
                    // and can't get hotter than maxTemp
                    // so maxTemp is least of those
                    maxTemp = Math.min(recipe.temperature+tolerance/2f, maxTemp);
                }
                // if this has water
                if (liquids.get(recipe.liquid.liquid) >= recipe.liquid.amount)
                    // same, can't get colder than recipe.temperature or minTemp
                    // so minTemp is biggest of them
                    minTemp = Math.max(recipe.temperature-tolerance/2f, minTemp);
            }
            temperature = energy / heatCapacity;
            if (temperature > maxTemp){
                latentEnergy = (temperature - maxTemp) * heatCapacity;
                temperature = maxTemp;
            } else if (temperature < minTemp) {
                latentEnergy = (temperature - minTemp) * heatCapacity;
                temperature = minTemp;
            }
        }
        public void updateCraft() {
            for(var recipe : recipes) {
                if (Mathf.equal(temperature, recipe.temperature, tolerance)) {
                    recipeLatent = recipe.latent;// update for visual
                    if (
                            latentEnergy >= recipe.latent &&
                                    items.get(recipe.solid.item) >= recipe.solid.amount &&
                                    liquids.get(recipe.liquid.liquid) + recipe.liquid.amount <= liquidCapacity
                    ) {
                        items.remove(recipe.solid);
                        liquids.add(recipe.liquid.liquid, recipe.liquid.amount);
                        latentEnergy -= recipe.latent;
                    } else if (
                            latentEnergy <= -recipe.latent &&
                                    liquids.get(recipe.liquid.liquid) >= recipe.liquid.amount &&
                                    items.total() + recipe.solid.amount <= itemCapacity
                    ) {
                        liquids.remove(recipe.liquid.liquid, recipe.liquid.amount);
                        items.add(recipe.solid.item, recipe.solid.amount);
                        latentEnergy -= -recipe.latent;
                    }
                }
            }
        }
        public void updateHeatExchange() {
            float deltaEnergy = edelta() * conductivity * (envTemperature - temperature);
            energy += deltaEnergy;
        }
        public float dayRadiation() {
            // solar + weather
            return state.rules.solarMultiplier * Attribute.light.env();
        }

        @Override
        public void updateTile() {
            updateHeatCapacity();
            updateTemperature();
            updateCraft();
            updateHeatExchange();
            dumpOutputs();
        }

        @Override
        public float progress() {
            float progress;
            if (recipeLatent > 0)
                progress = Math.abs(latentEnergy) / recipeLatent;
            else
                progress = 0f;
            return progress;
        }

        @Override
        public double sense(LAccess sensor){
            return switch (sensor) {
                case heat -> temperature;
                case efficiency -> envTemperature;
                case powerCapacity -> heatCapacity;
                case totalPower -> energy;
                default -> super.sense(sensor);
            };
        }

        @Override
        public void pickedUp(){
            envTemperature = ambientTemperature;
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            envTemperature = blockTemperature(tile.x, tile.y);
        }
        @Override
        public void write(Writes write){
            write.f(energy);
        }

        @Override
        public void read(Reads read, byte revision){
            energy = read.f();
        }
    }
}