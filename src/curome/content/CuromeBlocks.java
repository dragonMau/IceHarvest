package curome.content;

import curome.world.type.CuromeThermalRecipe;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawRegion;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import curome.world.blocks.CuromeIcebox;
//import curome.content.CuromeItems;
import curome.Logging;
import arc.struct.Seq;


import static mindustry.type.ItemStack.with;
//import static mindustry.type.ItemStack.requirements;

public class CuromeBlocks {
    public static Block icebox;
    
    public static final Seq<Block> all = new Seq<>();
    
    public static void load(){
        icebox = new CuromeIcebox("icebox"){{
            requirements(Category.crafting, with(
                Items.titanium, 40,
                Items.lead, 30,
                Items.metaglass, 15,
                Items.silicon, 15
            ));
            addRecipe(
                new CuromeThermalRecipe(){{
                    temperature = 0f;
                    latent = 334000f;
                    solid = new ItemStack(CuromeItems.ice, 1);
                    heatCapacitySolid = 2090f;
                    temperatureSolid = -10f;
                    liquid = new LiquidStack(Liquids.water, 30);
                    heatCapacityLiquid = 139.5f;
                    temperatureLiquid = 25f;
                }}
            );
            
            size = 2;
            itemCapacity = 30;
            liquidCapacity = 450f;
            
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawDefault());
        }};

        all.addAll(icebox);
        Logging.info("Loaded "+all.size+" blocks.");
    }
}