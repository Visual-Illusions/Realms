package net.visualillusionsent.realms.io;

import java.util.logging.Level;

public class RLevel extends Level {

    private static final long serialVersionUID = 210434042012L;

    public static final RLevel ANIMAL_DESTROY = new RLevel("ANIMAL_DESTROY", 6001),
            BLOCK_BREAK = new RLevel("BLOCK_BREAK", 6002),
            BLOCK_DESTROY = new RLevel("BLOCK_DESTROY", 6003),
            BLOCK_PHYSICS = new RLevel("BLOCK_PHYSICS", 6004),
            BLOCK_PLACE = new RLevel("BLOCK_PLACE", 6005),
            BLOCK_RIGHTCLICK = new RLevel("BLOCK_RIGHTCLICK", 6006),
            COMMAND = new RLevel("COMMAND", 6007),
            COMMAND_CHECK = new RLevel("COMMAND_CHECK", 6008),
            DAMAGE = new RLevel("DAMAGE", 6009),
            EAT = new RLevel("EAT", 6010),
            ENDERMAN_DROP = new RLevel("ENDERMAN_DROP", 6011),
            ENDERMAN_PICKUP = new RLevel("ENDERMAN_PICKUP", 6012),
            ENTITY_RIGHTCLICK = new RLevel("ENTITY_RIGHTCLICK", 6013),
            EXPLOSION = new RLevel("EXPLOSION", 6014),
            FLOW = new RLevel("FLOW", 6015),
            IGNITE = new RLevel("IGNITE", 6016),
            ITEM_DROP = new RLevel("ITEM_DROP", 6017),
            ITEM_PICKUP = new RLevel("ITEM_PICKUP", 6018),
            ITEM_USE = new RLevel("ITEM_USE", 6019),
            MOB_DESTROY = new RLevel("MOB_DESTROY", 6020),
            MOB_TARGET = new RLevel("MOB_TARGET", 6021),
            MOB_SPAWN = new RLevel("MOB_SPAWN", 6022),
            PISTON_EXTEND = new RLevel("PISTON_EXTEND", 6023),
            PISTON_RETRACT = new RLevel("PISTON_RETRACT", 6024),
            PORTAL_USE = new RLevel("PORTAL_USE", 6025),
            PLAYER_EXPLODE = new RLevel("PLAYER_EXPLODE", 6026),
            PLAYER_HEAL = new RLevel("PLAYER_HEAL", 6027),
            PLAYER_RESTRICT = new RLevel("PLAYER_RESTRICT", 6028),
            FOOD_EXHAUSTION = new RLevel("FOOD_EXHAUSTIONCHANGE", 6029),

            DEBUGINFO = new RLevel("DEBUGINFO", 6100),
            DEBUGWARNING = new RLevel("DEBUGWARNING", 6200),
            DEBUGSEVERE = new RLevel("DEBUGSEVERE", 6300);

    protected RLevel(String name, int intvalue) {
        super(name, intvalue);
    }
}
