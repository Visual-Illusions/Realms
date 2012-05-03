package net.visualillusionsent.realms.io;

import java.util.logging.Level;

public class RLevel extends Level{

    private static final long serialVersionUID = 210434042012L;
    
    public static final RLevel CANUSECOMMAND        = new RLevel("canPlayerUseCommand", 6001),
                               BLOCK_BREAK          = new RLevel("onBlockBreak", 6002),
                               BLOCK_DESTROY        = new RLevel("onBlockDestroy", 6003),
                               BLOCK_PHYSICS        = new RLevel("onBlockPhysics", 6004),
                               BLOCK_PLACE          = new RLevel("onBlockPlace", 6005),
                               BLOCK_RIGHTCLICK     = new RLevel("onBlockRightClick", 6006),
                               COMMAND              = new RLevel("onCommand", 6007),
                               DAMAGE               = new RLevel("onDamage", 6008),
                               EAT                  = new RLevel("onEat", 6009),
                               ENDERMAN_DROP        = new RLevel("onEndermanDrop", 6010),
                               ENDERMAN_PICKUP      = new RLevel("onEndermanPickUp", 6011),
                               ENTITY_RIGHTCLICK    = new RLevel("onEntityRightClick", 6012),
                               EXPLOSION            = new RLevel("onExplosion", 6013),
                               FLOW                 = new RLevel("onFlow", 6014),
                               IGNITE               = new RLevel("onIgnite", 6015),
                               ITEM_DROP            = new RLevel("onItemDrop", 6016),
                               ITEM_PICKUP          = new RLevel("onItemPickUp", 6017),
                               ITEM_USE             = new RLevel("onItemUse", 6018),
                               MOB_DESTROY          = new RLevel("MobDestructor", 6019),
                               MOB_TARGET           = new RLevel("onMobTarget", 6020),
                               MOB_SPAWN            = new RLevel("onMobSpawn", 6021),
                               PISTON_EXTEND        = new RLevel("onPistonExtend", 6022),
                               PISTON_RETRACT       = new RLevel("onPistonRetract", 6023),
                               PORTAL_USE           = new RLevel("onPortalUse", 6024),
                               PLAYER_EXPLODE       = new RLevel("PlayerExplode", 6025),
                               PLAYER_HEAL          = new RLevel("PlayerHeal", 6026),
                               PLAYER_RESTRICT      = new RLevel("PlayerRestrict", 6027),
                               ANIMAL_DESTROY       = new RLevel("AnimalDestory", 6028);
    
    public static final RLevel DEBUGINFO = new RLevel("DEBUGINFO", 6030);
    public static final RLevel DEBUGWARNING = new RLevel("DEBUGWARNING", 6040);
    public static final RLevel DEBUGSEVERE = new RLevel("DEBUGSEVERE", 6050);
    
    protected RLevel(String arg0, int arg1) {
        super(arg0, arg1);
    }
}
