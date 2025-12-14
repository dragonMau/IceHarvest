package curome.content;

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
import curome.content.CuromeItems;
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
            
            consumeItem(CuromeItems.ice);
            consumeLiquid(Liquids.water, 30f/120f);
            
            craftTime = 120;
            outputItem = new ItemStack(CuromeItems.ice, 1);
            outputLiquid = new LiquidStack(Liquids.water, 30f / 120f);
            
            baseEfficiency = 0f;
            minEfficiency = -8f;
            maxBoost = 8f;
            scaleLiquidConsumption = true;
            
            size = 2;
            itemCapacity = 30;
            liquidCapacity = 450f;
            
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawLiquidTile(Liquids.cryofluid){{drawLiquidLight = true;}}, new DrawDefault());
        }};
        
        Logging.info("Loaded "+all.size+" blocks.");
    }
}