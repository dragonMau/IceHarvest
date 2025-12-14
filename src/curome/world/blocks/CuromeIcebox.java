package curome.world.blocks;

//import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;

import mindustry.world.blocks.liquid.Conduit.ConduitBuild;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.AttributeCrafter.AttributeCrafterBuild;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Attribute;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawRegion;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import mindustry.world.Tile;
import mindustry.world.Block;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.logic.LAccess;

import arc.graphics.g2d.TextureRegion;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.struct.EnumSet;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.io.Writes;
import arc.util.io.Reads;
import arc.math.Mathf;
import arc.math.geom.Geometry;


import curome.content.CuromeItems;

import static mindustry.Vars.world;
import static mindustry.Vars.tilesize;

public class CuromeIcebox extends AttributeCrafter {
    /*
     * Automatically pushes items out
     * Automatically pushes specific liquid out
     *     (Liquids.water)
     * Accepts specific item type (CuromeItems.ice)
     * Accepts 3 types of liquids, to separate stacks
     *     (Liquids.slug, Liquids.cryofluid, Liquids.water)
     *
     */
    public CuromeIcebox(String name){
        super(name);
    }
    public class CuromeIceboxBuild extends AttributeCrafterBuild{
        
    }
}