package curome.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;
import curome.Logging;

public class CuromeItems {
    public static Item ice;
    
    public static final Seq<Item> all = new Seq<>();
    
    public static void load(){
        ice = new Item("ice", Color.valueOf("a29fff")){{
            lowPriority = true;
            buildable = false;
            hardness = 3;
        }};
        
        all.addAll(ice);
        
        Logging.info("Loaded "+all.size+" items.");
    }
}