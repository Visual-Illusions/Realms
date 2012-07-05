import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.exception.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.exception.InvaildZoneFlagException;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.Zone.ZoneFlag;
import net.visualillusionsent.realms.zones.ZoneLists;

/**
 * Realms Custom Hooks Class
 * 
 * @author Jason Jones
 */
public class RealmsHooks {
    private RHandle rhandle;
    final PluginInterface PermissionCheck = new PermissionCheck();
    final PluginInterface ZoneCheck = new ZoneCheck();
    final PluginInterface PermissionChange = new PermissionChange();
    final PluginInterface ZoneChange = new ZoneChange();
    final PluginInterface ZoneFlagCheck = new ZoneFlagCheck();

    private String NL = System.getProperty("line.separator");

    public RealmsHooks(Realms realms, RHandle rhandle) {
        this.rhandle = rhandle;
    }

    /**
     * Permission Check Custom Hook
     */
    public final class PermissionCheck implements PluginInterface {
        @Override
        public final String getName() {
            return "Realms-PermissionCheck";
        }

        @Override
        public final int getNumParameters() {
            return 3;
        }

        @Override
        public final String checkParameters(Object[] args) {
            if (args.length != getNumParameters()) {
                return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' String PermType, Player player, Block block/String ZoneName '";
            }
            return null;
        }

        /**
         * Permission Check
         * 
         * @param PermType
         * @param Player
         * @Param Block or Zone Name
         * 
         * @return true if the player has permission, false if not or error
         */
        @Override
        public final Object run(Object[] args) {
            String permType = null;
            Player player = null;
            Block block = null;
            String ZoneName = null;

            if (args[0] instanceof String) {
                permType = (String) args[0];
            }

            if (args[1] instanceof Player) {
                player = (Player) args[1];
            }

            if (args[2] instanceof Block) {
                block = (Block) args[2];
            }
            else if (args[2] instanceof String) {
                ZoneName = (String) args[2];
            }

            if (permType != null && player != null && (block != null || ZoneName != null)) {
                Permission.PermType Type = null;

                try {
                    Type = Permission.PermType.getTypeFromString(permType.toUpperCase());
                }
                catch (InvaildPermissionTypeException IPTE) {
                    rhandle.log(Level.WARNING, "Another plugin requested a PermType that was INVAILD!");
                    return false;
                }

                if (block != null) {
                    CModBlock cmb = new CModBlock(block);
                    Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cmb), cmb);
                    return zone.permissionCheck(new CModPlayer(player), Type);
                }
                else if (ZoneName != null) {
                    try {
                        Zone zone = ZoneLists.getZoneByName(ZoneName);
                        zone.permissionCheck(new CModPlayer(player), Type);
                    }
                    catch (ZoneNotFoundException e) {
                        rhandle.log(Level.WARNING, "Another plugin gave a Zone name that was INVAILD!");
                        return false;
                    }
                }
            }
            return false;
        }
    }

    /**
     * ZoneCheck Custom Hook
     */
    public final class ZoneCheck implements PluginInterface {
        @Override
        public final String getName() {
            return "Realms-ZoneCheck";
        }

        @Override
        public final int getNumParameters() {
            return 2;
        }

        @Override
        public final String checkParameters(Object[] args) {
            if (args[0] instanceof String) {
                String cmd = (String) args[0];
                if (cmd.equals("Name-Check")) {
                    if (args.length != 2) {
                        return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' String ZoneName '";
                    }
                }
                else if (cmd.equals("Player-Zone")) {
                    if (args.length != 2) {
                        return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' Player player '";
                    }
                }
                else {
                    return "[Realms] Invalid ZoneCheck Command. Vaild Commands: 'Name-Check', 'Player-Zone'";
                }
                return null;
            }
            return "[Realms] Invalid ZoneCheck Command. Vaild Commands: 'Name-Check', 'Player-Zone'";
        }

        /**
         * Zone Check
         * 
         * @param Zone
         *            Name
         */
        @Override
        public final Object run(Object[] args) {
            String cmd = (String) args[0];
            String ZoneName = null;
            Player player = null;

            if (args[1] instanceof String) {
                ZoneName = (String) args[1];
            }
            else if (args[1] instanceof Player) {
                player = (Player) args[1];
            }

            if (ZoneName != null && cmd.equalsIgnoreCase("Name-Check")) {
                try {
                    ZoneLists.getZoneByName(ZoneName);
                    return true;
                }
                catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.WARNING, "Another plugin gave a Zone name that was INVAILD!");
                }
            }
            else if (player != null && cmd.equalsIgnoreCase("Player-Zone")) {
                List<Zone> zones = ZoneLists.getplayerZones(new CModPlayer(player));
                String[] names = new String[zones.size()];
                try {
                    for (int i = 0; i < zones.size(); i++) {
                        names[i] = zones.get(i).getName();
                    }
                }
                catch (ConcurrentModificationException CME) {
                    return new String[] { "derp" };
                }
                return names;
            }
            return false;
        }
    }

    /**
     * Zone Flag Check Custom Hook
     */
    public final class ZoneFlagCheck implements PluginInterface {
        @Override
        public final String getName() {
            return "Realms-ZoneFlagCheck";
        }

        @Override
        public final int getNumParameters() {
            return 2;
        }

        @Override
        public final String checkParameters(Object[] args) {
            if (args.length != getNumParameters()) {
                return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' String Flag, Player player/Block block/String ZoneName '";
            }
            return null;
        }

        /**
         * Zone Flags Check
         * 
         * @param Flag
         * @param Player
         *            or Block or ZoneName
         * @return true if flag is set, false if not or error
         */
        @Override
        public final Object run(Object[] args) {
            String Flag = null;
            Player player = null;
            Block block = null;
            String ZoneName = null;
            Zone zone = null;

            if (args[0] instanceof String) {
                Flag = (String) args[0];
            }

            if (args[1] instanceof Player) {
                player = (Player) args[1];
            }
            else if (args[1] instanceof Block) {
                block = (Block) args[1];
            }
            else if (args[1] instanceof String) {
                ZoneName = (String) args[1];
            }

            if (Flag != null) {
                try {
                    if (player != null) {
                        CModPlayer cmp = new CModPlayer(player);
                        zone = ZoneLists.getZone(rhandle.getEverywhere(cmp), cmp);
                    }
                    else if (block != null) {
                        CModBlock cmb = new CModBlock(block);
                        zone = ZoneLists.getZone(rhandle.getEverywhere(cmb), cmb);
                    }
                    else if (ZoneName != null) {
                        zone = ZoneLists.getZoneByName(ZoneName);
                    }

                    if (zone != null) {
                        if (Flag.equalsIgnoreCase("PVP")) {
                            return zone.getPVP();
                        }
                        else if (Flag.equalsIgnoreCase("Sanctuary")) {
                            return zone.getSanctuary();
                        }
                        else if (Flag.equalsIgnoreCase("Creeper")) {
                            return zone.getCreeper();
                        }
                        else if (Flag.equalsIgnoreCase("Ghast")) {
                            return zone.getGhast();
                        }
                        else if (Flag.equalsIgnoreCase("Fall")) {
                            return zone.getFall();
                        }
                        else if (Flag.equalsIgnoreCase("Suffocate")) {
                            return zone.getSuffocate();
                        }
                        else if (Flag.equalsIgnoreCase("Fire")) {
                            return zone.getFire();
                        }
                        else if (Flag.equalsIgnoreCase("Animals")) {
                            return zone.getAnimals();
                        }
                        else if (Flag.equalsIgnoreCase("Physics")) {
                            return zone.getPhysics();
                        }
                        else if (Flag.equalsIgnoreCase("Creative")) {
                            return zone.getCreative();
                        }
                        else if (Flag.equalsIgnoreCase("Pistons")) {
                            return zone.getPistons();
                        }
                        else if (Flag.equalsIgnoreCase("Healing")) {
                            return zone.getHealing();
                        }
                        else if (Flag.equalsIgnoreCase("Enderman")) {
                            return zone.getEnderman();
                        }
                        else if (Flag.equalsIgnoreCase("Spread")) {
                            return zone.getSpread();
                        }
                        else if (Flag.equalsIgnoreCase("Flow")) {
                            return zone.getFlow();
                        }
                        else if (Flag.equalsIgnoreCase("TNT")) {
                            return zone.getTNT();
                        }
                        else if (Flag.equalsIgnoreCase("Potion")) {
                            return zone.getPotion();
                        }
                        else if (Flag.equalsIgnoreCase("Starve")) {
                            return zone.getStarve();
                        }
                        else if (Flag.equalsIgnoreCase("Restricted")) {
                            return zone.getRestricted();
                        }
                    }
                }
                catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.WARNING, "Another plugin gave a Zone name that was INVAILD!");
                }
            }
            return false;
        }
    }

    /**
     * Permission Change Custom Hook
     */
    public final class PermissionChange implements PluginInterface {
        @Override
        public final String getName() {
            return "Realms-PermissionChange";
        }

        @Override
        public final int getNumParameters() {
            return 4;
        }

        @Override
        public final String checkParameters(Object[] args) {
            if (args.length != getNumParameters()) {
                return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' String PlayerName, String PermType, String ZoneName, Boolean Grant '";
            }
            return null;
        }

        /**
         * Permission Change
         * 
         * @Param PlayerName
         * @Param Permission Type
         * @Param Zone Name
         * @Param Grant (Boolean)
         */
        @Override
        public final Object run(Object[] args) {

            String name = null;
            String type = null;
            String ZoneName = null;
            boolean grant = (Boolean) null;

            if (args[0] instanceof String) {
                name = (String) args[0];
            }

            if (args[1] instanceof String) {
                type = (String) args[1];
            }

            if (args[2] instanceof String) {
                ZoneName = (String) args[2];
            }

            if (args[3] instanceof Boolean) {
                grant = (Boolean) args[3];
            }

            if (name != null && type != null && ZoneName != null && grant != (Boolean) null) {
                try {
                    Zone zone = ZoneLists.getZoneByName(ZoneName);
                    Permission.PermType permtype = Permission.PermType.getTypeFromString(type);
                    zone.setPermission(name, permtype, grant, false);
                    return true;
                }
                catch (InvaildPermissionTypeException e) {
                    rhandle.log(Level.WARNING, "Another plugin requested a PermType change that was INVAILD!");
                }
                catch (ZoneNotFoundException e) {
                    rhandle.log(Level.WARNING, "Another plugin gave a Zone name that was INVAILD!");
                }
            }
            else {
                rhandle.log(Level.WARNING, "Another plugin gave a parameter for PermissionChange that was NULL!");
            }
            return false;
        }
    }

    /**
     * Zone Flags Change
     */
    public final class ZoneChange implements PluginInterface {
        @Override
        public final String getName() {
            return "Realms-ZoneChange";
        }

        @Override
        public final int getNumParameters() {
            return 3;
        }

        @Override
        public final String checkParameters(Object[] args) {
            if (args.length != getNumParameters()) {
                return "[Realms] Invalid amount of parameters." + NL + "Proper Parameters are:' String ZoneName, String Flag, String ON/OFF/INHERIT '";
            }
            return null;
        }

        /**
         * Permission Change
         * 
         * @Param Zone Name
         * @Param Flag Type
         * @Param ON/OFF
         */
        @Override
        public final Object run(Object[] args) {
            String zoneName = (String) args[0];
            String Type = (String) args[1];
            String Setting = (String) args[2];
            try {
                Zone zone = ZoneLists.getZoneByName(zoneName);
                ZoneFlag theType = ZoneFlag.getZoneFlag(Setting.toUpperCase());
                if (zone.getName().equals("everywhere") && theType.equals(ZoneFlag.INHERIT)) {
                    return false;
                }
                if (Type.equalsIgnoreCase("PVP")) {
                    zone.setPVP(theType);
                }
                else if (Type.equalsIgnoreCase("Sanctuary")) {
                    zone.setSanctuary(theType);
                }
                else if (Type.equalsIgnoreCase("Creeper")) {
                    zone.setCreeper(theType);
                }
                else if (Type.equalsIgnoreCase("Ghast")) {
                    zone.setGhast(theType);
                }
                else if (Type.equalsIgnoreCase("Fall")) {
                    zone.setFall(theType);
                }
                else if (Type.equalsIgnoreCase("Suffocate")) {
                    zone.setSuffocate(theType);
                }
                else if (Type.equalsIgnoreCase("Fire")) {
                    zone.setFire(theType);
                }
                else if (Type.equalsIgnoreCase("Animals")) {
                    zone.setAnimals(theType);
                }
                else if (Type.equalsIgnoreCase("Physics")) {
                    zone.setPhysics(theType);
                }
                else if (Type.equalsIgnoreCase("Creative")) {
                    zone.setCreative(theType);
                }
                else if (Type.equalsIgnoreCase("Pistons")) {
                    zone.setPistons(theType);
                }
                else if (Type.equalsIgnoreCase("Healing")) {
                    zone.setHealing(theType);
                }
                else if (Type.equalsIgnoreCase("Enderman")) {
                    zone.setEnderman(theType);
                }
                else if (Type.equalsIgnoreCase("Spread")) {
                    zone.setSpread(theType);
                }
                else if (Type.equalsIgnoreCase("Flow")) {
                    zone.setFlow(theType);
                }
                else if (Type.equalsIgnoreCase("TNT")) {
                    zone.setTNT(theType);
                }
                else if (Type.equalsIgnoreCase("Potion")) {
                    zone.setPotion(theType);
                }
                else if (Type.equalsIgnoreCase("Starve")) {
                    zone.setStarve(theType);
                }
                else if (Type.equalsIgnoreCase("Restricted")) {
                    zone.setRestricted(theType);
                }
                else {
                    rhandle.log(Level.WARNING, "Another plugin requested a ZoneType change that was INVAILD!");
                    return false;
                }
            }
            catch (InvaildZoneFlagException IZTE) {
                rhandle.log(Level.WARNING, "Another plugin requested a ZoneType change that was INVAILD!");
                return false;
            }
            catch (ZoneNotFoundException ZNFE) {
                rhandle.log(Level.WARNING, "Another plugin gave a Zone name that was INVAILD!");
                return false;
            }
            return true;
        }
    }
}
