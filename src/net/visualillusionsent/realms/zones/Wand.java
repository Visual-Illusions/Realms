package net.visualillusionsent.realms.zones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.polygons.Point;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ChatColor;

/**
 * Realms Wand Class
 * 
 * @author darkdiplomat
 */
public class Wand {
    int pylonType;
    int pylonHeight;

    // Dynamically generated
    RHandle rhandle;
    RealmsProps props;
    ICModPlayer player;
    
    // Read from file
    String mode = "default";
    PolygonArea workingPolygon;
    private List<ICModBlock> savedBlocks = new ArrayList<ICModBlock>();

    public Wand(RHandle rhandle, ICModPlayer player){
        this.rhandle = rhandle;
        //this.props = realm.props;
        this.pylonType = RealmsProps.getpylontype();
        this.pylonHeight = RealmsProps.getpylonheight();
        this.player = player;
    }

    // Reset wand to default mode
    public void reset() {
        if(!mode.equalsIgnoreCase("default")) {
            workingPolygon.cancelEdit();
            workingPolygon = null;
            mode = "default";
        }
        resetAllSavedBlocks();
    }

    // Reset all saved blocks in the column x,z
    public void removePylon(int x, int z) {
        Iterator<ICModBlock> itr = savedBlocks.iterator(); 
        while(itr.hasNext()) {
            ICModBlock block = itr.next();
            if(block.getX() == x && block.getZ() == z) {
                rhandle.getServer().setBlock(block);
                itr.remove();
            }
        }
    }

    // Resets all saved blocks
    public void resetAllSavedBlocks() {
        for(ICModBlock block : savedBlocks) {
            rhandle.getServer().setBlock(block);
        }
        savedBlocks = new ArrayList<ICModBlock>();
    }

    // Add saved block
    public void addSavedBlock(ICModBlock block) {
        savedBlocks.add(block);
    }

    // Creates a pylon above the specified block.
    // Saves the original blocks into wand's savedBlocks list
    public void createPylon(ICModBlock block) {
        for (int i = 0; i < pylonHeight; i++) {
            ICModBlock storeblock = rhandle.getServer().getBlockAt(block.getX(), block.getY()+i, block.getZ(), block.getDimIndex(), block.getWorldName());
            addSavedBlock(storeblock);
            rhandle.getServer().setBlock(pylonType, block.getX(), block.getY()+i, block.getZ(), block.getWorldName(), block.getDimIndex());
        }
    }
    
    public void createPylon(Point point, String world, int dim) {
        for (int i = 0; i < pylonHeight; i++) {
            ICModBlock storeblock = rhandle.getServer().getBlockAt(point.x, point.y+i, point.z, dim, world);
            addSavedBlock(storeblock);
            rhandle.getServer().setBlock(pylonType, point.x, point.y+i, point.z, world, dim);
        }
    }

    // WAND COMMANDS
    public boolean wandCommand(ICModPlayer player, String[] command) {
        if(command.length < 2) {
            player.notify("No /wand subcommand provided!");
            return true;
        }

        // Cancel operation
        if(command[1].equalsIgnoreCase("cancel")) {
            this.reset();
            player.sendMessage(ChatColor.CYAN+"Your wand has been reset.");
            return true;
        }
        
        // Reset operation
        if(command[1].equalsIgnoreCase("reset")) {
            String zoneName = workingPolygon.getZone().getName();
            this.resetAllSavedBlocks();
            workingPolygon.getVertices().clear();
            workingPolygon.save();
            player.sendMessage(ChatColor.CYAN+"All pylons removed.");
            
            command = new String[]{"/wand", "edit", zoneName};
        }

        // Save vertices
        if (command[1].equalsIgnoreCase("save")) {
            if(!mode.equalsIgnoreCase("default")) {
                if (workingPolygon.workingVerticesCleared()) {
                    player.sendMessage(ChatColor.CYAN+"Zone saved with no pylons. Wand back in "+ChatColor.YELLOW+"'getInfo'"+ChatColor.CYAN+" mode.");
                    workingPolygon.save();
                    reset();
                    return true;
                } else {
                    if(!workingPolygon.validPolygon(player)){
                        return true;
                    }
                    workingPolygon.save();
                    reset();
                    player.sendMessage(ChatColor.ORANGE+"Zone complete!"+ChatColor.CYAN+" Wand back in "+ChatColor.YELLOW+"'getInfo'"+ChatColor.CYAN+" mode.");
                    return true;
                }
            }
            player.notify("You are not currently editing a zone");
            return true;
        }

        // Show vertices
        if (command[1].equalsIgnoreCase("show")) {
            int x1, z1, x2, z2;
            PolygonArea thePolygon = null;
            
            switch (command.length) {
            case 1:
                player.notify("§bUsage: /wand show [x1,z1 x2,z2] [zonename]");
                return true;
            case 2:
                if (!mode.equalsIgnoreCase("polygon")){
                    player.notify("§bUsage: /wand show [x1,z1 x2,z2] [zonename]");
                    return true;
                }
                else{
                    thePolygon = workingPolygon;
                }
            case 3:
                if (thePolygon == null) {
                    try{
                        Zone zone = ZoneLists.getZoneByName(command[2]);
                        thePolygon = zone.getPolygon();
                    }
                    catch (ZoneNotFoundException ZNFE) {
                        player.notify("The zone §6'" + command[2] + "'§c could not be found!");
                        return true;
                    }
                }
                
                if (thePolygon == null || thePolygon.getVertices().isEmpty()){
                    player.notify("The zone doesn't have any vertices!");
                    return true;
                }
                
                x1 = thePolygon.getVertices().get(0).x;
                x2 = thePolygon.getVertices().get(0).x;
                z1 = thePolygon.getVertices().get(0).z;
                z2 = thePolygon.getVertices().get(0).z;
                for (Point p : thePolygon.getVertices()) {
                    if (p.x < x1) x1 = p.x;
                    if (p.x > x2) x2 = p.x;
                    if (p.z < z1) z1 = p.z;
                    if (p.z > z2) z2 = p.z;
                }
                
//                int radius = (int) Math.ceil(thePolygon.getRadius());
//                int fudge = (int) Math.floor(radius / 10);
//                x1 = thePolygon.getCentroid().x - radius - fudge;
//                x2 = thePolygon.getCentroid().x + radius + fudge;
//                z1 = thePolygon.getCentroid().z - radius - fudge;
//                z2 = thePolygon.getCentroid().z + radius + fudge;
                
                player.sendMessage("§bUsing bounding coords of: (§6" + x1 + "§b,§6" + z1 + "§b) (§6" + x2 + "§b,§6" + z2 + "§b)");
                
                break;
            case 4:
                if (mode.equalsIgnoreCase("default")){
                    player.notify("§bUsage: /wand show [x1,z1 x2,z2] [zonename]");
                    return true;
                }
                else{
                    thePolygon = workingPolygon;
                }
            default:
                try {
                    String[] coord1 = command[2].split(",");
                    String[] coord2 = command[3].split(",");
                    if (coord1.length < 2 || coord2.length < 2){
                        player.notify("§bUsage: /wand show [x1,z1 x2,z2] [zonename]");
                        return true;
                    }
                    x1 = Integer.parseInt(coord1[0]);
                    z1 = Integer.parseInt(coord1[1]);
                    x2 = Integer.parseInt(coord2[0]);
                    z2 = Integer.parseInt(coord2[1]);
                } catch (NumberFormatException e) {
                    player.notify("§bUsage: /wand show [x1,z1 x2,z2] [zonename]");
                    return true;
                }
                if (thePolygon == null) {
                    try{
                        Zone zone = ZoneLists.getZoneByName(command[4]);
                        thePolygon = zone.getPolygon();
                    }
                    catch (ZoneNotFoundException ZNFE) {
                        player.notify("The zone §6'" + command[4] + "'§c could not be found!");
                        return true;
                    }
                }
                break;
            }
            
            int scalex = (int) Math.floor((Math.abs(x1 - x2) - 20)/40.) + 1;
            if (scalex < 1) scalex = 1;
            int countx = (int) Math.ceil(Math.abs(x1 - x2) / scalex);
            int scalez = (int) Math.floor((Math.abs(z1 - z2) - 7)/14.) + 1;
            if (scalez < 1) scalez = 1;
            int countz = (int) Math.ceil(Math.abs(z1- z2) / scalez);
            
            if (command.length < 4) {
                x1 = x1 - scalex;
                x2 = x2 + scalex;
                z1 = z1 - scalez;
                z2 = z2 + scalez;
            }
            
            if (scalex > 1){
                player.sendMessage("§bUsing x scaling factor of §e" + scalex);
            }
            if (scalez > 1){
                player.sendMessage("§bUsing z scaling factor of §e" + scalez);
            }
            
            int i, j = 0;
            
            for (i = -1; i < countz; i++) {
                boolean color = false;
                boolean first = false;
                
                int zcoord = i * scalez + z1 + (int) Math.floor(scalez / 2);    
                StringBuilder messageString = new StringBuilder("§2");
                
                if (i == -1) messageString.append(" ");
                else messageString.append(Math.abs(zcoord) % 10);
                
                for (j = 0; j < countx; j++) {
                    
                    int xcoord = j * scalex + x1 + (int) Math.floor(scalex / 2);
                    
                    if (i == -1) {
                        messageString.append(Math.abs(xcoord) % 10);
                        continue;
                    }
                    
                    if (PolygonArea.contains(thePolygon.getVertices(), new Point(xcoord, 64, zcoord), 0, 128)) { 
                        if (!color || !first) {
                            messageString.append("§8");
                            color = true;
                            first = true;
                        }
                        messageString.append("X");
                    } else {
                        if (color || !first) {
                            messageString.append("§f");
                            color = false;
                            first = true;
                        }
                        messageString.append("-");
                    }
                }
                
                player.sendMessage(messageString.toString());
            }
            
            return true;
        }
        
        // Edit zone
        if(command[1].equalsIgnoreCase("edit")) {

            // Wand must be in default mode
            if(!mode.equalsIgnoreCase("default")){
                player.notify("You must finish what you are doing first (or cancel it)");
                return true;
            }

            // Zone name must be provided
            if(command.length < 3){
                player.notify("No zone name provided");
                return true;
            }

            // Cannot edit the "everywhere" zone!
            if(command[2].equalsIgnoreCase("everywhere")){
                player.notify("You cannot edit the §6EVERYWHERE§c zone!");
                return true;
            }

            // Get zone
            try {
                Zone zone = ZoneLists.getZoneByName(command[2]);
                
                if(zone.getParent() == null){
                    player.notify("The zone §6'" + command[2] + "'§c does not have a parent zone!");
                    return true;
                }
                if(zone.getPolygon() == null){
                    zone.setPolygon(new PolygonArea(rhandle, zone));
                }
                // Zone must be in "saved" mode
                if(!zone.getPolygon().getMode().equalsIgnoreCase("saved")){
                    player.notify("That zone is already being edited!");
                    return true;
                }

                // Player must have zoning permission
                if(!zone.permissionCheck(player, Permission.PermType.ZONING)){
                    player.notify("You do not have permission to edit or create zones within §6" + zone.getParent().getName() + "§c!");
                    return true;
                }
                
                if(!zone.getWorld().equals(player.getWorldName())){
                    player.notify("You need to be in the same world as the zone!");
                    return true;
                }
                else if (zone.getDimension() != player.getDimIndex()){
                    player.notify("You need to be in the same dimension as the zone!");
                    return true;
                }
                
                // Passed all checks!
                mode = "polygon";
                workingPolygon = zone.getPolygon();
                List<Point> oldVertices = workingPolygon.edit();
                for(Point p : oldVertices){
                    createPylon(p, player.getWorldName(), player.getDimIndex());
                }
                player.sendMessage("§bYour wand is now ready to edit §6" + zone.getName());
                return true;
            }
            catch (ZoneNotFoundException ZNFE) {
                player.notify("The zone §6'" + command[2] + "'§c could not be found!"); 
                return true;
            }
        }

        // setFloor
        if(command[1].equalsIgnoreCase("setfloor") && mode.equalsIgnoreCase("polygon")) {
            if(command.length == 3) {
                try {
                    int floor = Integer.parseInt(command[2]);
                    workingPolygon.setWorkingFloor(floor);
                    player.sendMessage("§bSet zone §6" + workingPolygon.getZone().getName() + "§b floor to §e" + floor);
                    return true;
                } catch(NumberFormatException NFE) {
                    player.notify("Could not understand floor value: §e" + command[2]);
                    return true;
                }
            } else {
                mode = "setFloor";
                player.sendMessage("§bYour wand is now in set zone §eFLOOR§b mode for zone §6" + workingPolygon.getZone().getName());
                return true;
            }
        }

        // setCeiling
        if(command[1].equalsIgnoreCase("setceiling") && mode.equalsIgnoreCase("polygon")) {
            if(command.length == 3) {
                try {
                    int ceiling = Integer.parseInt(command[2]);
                    workingPolygon.setWorkingCeiling(ceiling);
                    player.sendMessage("§bSet zone §6" + workingPolygon.getZone().getName() + "§b ceiling to §e" + ceiling);
                    return true;
                } catch(Exception e) {
                    player.notify("Could not understand ceiling value: §e" + command[2]);
                    return true;
                }
            } else {
                mode = "setCeiling";
                player.sendMessage("§bYour wand is now in set zone §eCEILING§b mode for zone §6" + workingPolygon.getZone().getName());
                return true;
            }
        }
        
        // None of the above
        player.notify("/wand command not understood!");
        return true;
    }

    // WAND CLICK

    public boolean wandClick(ICModPlayer player, ICModBlock block) {
       
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String worldname = player.getWorldName();
        int dimindex = player.getDimIndex();

        // By default wand is in get info mode
        if(mode.equalsIgnoreCase("default")) {
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(worldname, dimindex), block);
            player.sendMessage("§b---§e"+zone.getName()+"§b---");
            
            if(zone.getPolygon() != null){
                StringBuilder points = new StringBuilder();
                String[] poin = zone.whichChildContains(block).getPolygon().toString().split(",");
                int i = 0;
                for(i = 2; i < poin.length; i += 3){
                    points.append("("+poin[i]+","+poin[i+2]+") ");
                    if(i+3 > 23 && i+3 < poin.length){
                        points.append("...");
                        break;
                    }
                }
                player.sendMessage("§6POINTS: §7"+points.toString());
            }
            
            String[] flags = zone.getFlags(true, true);
            for(String mess : flags){
                player.sendMessage(mess);
            }
            return true;
        }

        // workingPolygon must not be null for remaining wand actions
        if(workingPolygon == null) {
            player.sendMessage("§cWand has no zone selected! Resetting wand.");
            reset();
            return true;
        }
        
        if(workingPolygon.getZone().getWorld() == null){
            workingPolygon.getZone().setWorld(player.getWorldName());
            workingPolygon.getZone().setDimension(player.getDimIndex());
        }
        else if (!workingPolygon.getZone().getWorld().equals(worldname)){
            player.notify("You need to be in the same world as the zone! Resetting wand.");
            reset();
            return true;
        }
       
        if(mode.equalsIgnoreCase("setCeiling")) {
            workingPolygon.setWorkingCeiling(y);
            mode = "polygon";
            player.sendMessage("§bSet zone §6" + workingPolygon.getZone().getName() + "§b ceiling to §e" + y);
            player.sendMessage("§bWand mode set to define §6" + workingPolygon.getZone().getName());
            return true;
        }

        if(mode.equalsIgnoreCase("setFloor")) {
            workingPolygon.setWorkingFloor(y);
            mode = "polygon";
            player.sendMessage("§bSet zone §6" + workingPolygon.getZone().getName() + "§b floor to §e" + y);
            player.sendMessage("§bWand mode set to define §6" + workingPolygon.getZone().getName());
            return true;
        }

        if(mode.equalsIgnoreCase("polygon")) {
            // Remove last vertex
            if(workingPolygon.containsWorkingVertex(block)) {
                workingPolygon.removeWorkingVertex(block);
                removePylon(block.getX(), block.getZ());
                player.sendMessage("§bVertex §e" + x + "§b,§e" + z + "§b removed from zone §6" + workingPolygon.getZone().getName());
                return true;
            }
            // Check chests
            for (int i = 0; i < pylonHeight; i++) {
                ICModBlock testblock = rhandle.getServer().getBlockAt(block.getX(), block.getY()+i, block.getZ(), dimindex, worldname);
                if (testblock.getType() == 54){
                    player.notify("You may not place a pylon where there's a chest! Remove it first.");
                    return true;
                }
            }
            
            // The vertex must be valid
            if(!workingPolygon.validVertex(player, block)){
                return true;
            }

            List<Point> removedVertices = workingPolygon.addVertex(player, block);
            for(Point p : removedVertices) {
                player.sendMessage("§bRemoving vertex at §e" + p.x + "§b,§e" + p.y + "§b,§e" + p.z);
                removePylon(p.x, p.z);
            }
            createPylon(block);
            player.sendMessage("§bVertex §e" + x + "§b,§e" + z + "§b added to zone §6" + workingPolygon.getZone().getName());
            return true;
        }

        player.sendMessage("§cWand mode invalid. Reseting wand now.");
        reset();
        return true;
    }
}
