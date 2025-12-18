package curome;

import arc.util.Log;
import curome.Logging;
import curome.content.CuromeBlocks;
import curome.content.CuromeItems;
import mindustry.mod.Mod;

public class IceHarvest extends Mod{
    public IceHarvest(){
        Logging.info("Loaded IceHarvest mod constructor.");
    }

    @Override
    public void loadContent(){
        Log.level = Log.LogLevel.debug;
        Logging.info("Loading content.");
        CuromeItems.load();
        CuromeBlocks.load();
    }

}
