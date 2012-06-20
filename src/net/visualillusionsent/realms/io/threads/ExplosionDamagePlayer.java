package net.visualillusionsent.realms.io.threads;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Explosion Damage Player thread
 * <p>
 * Called if player damage from explosions is enabled
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class ExplosionDamagePlayer extends Thread{
    private RHandle rhandle;
    private List<ICModPlayer> scanList;
    private ICModBlock block;
    private String debug = "Player - Name: '%s' @ Location: X: '%s' Y: '%s' Z: '%s' World: '%s' Dimension: '%s'";
        
    /**
     * class constructor
     *  
     * @param Realm
     * @param RLocation
     */
    public ExplosionDamagePlayer (RHandle rhandle, ICModBlock block) {
        this.rhandle = rhandle;
        this.block = block;
        scanList = new ArrayList<ICModPlayer>();
        setName("RealmsPlayerExplosionDamage-Thread");
    }
    
    /**
     * Realms Player Explosion Damage Run
     */
    public void run() {
        try{
            /*Player Lists*/
            scanList = rhandle.getServer().getPlayerList();

            /*Check Player List*/
            if (!scanList.isEmpty()){
                synchronized(scanList){
                    for(ICModPlayer thePlayer : scanList){
                        if(inRange(thePlayer)){
                            Zone theZone = ZoneLists.getZone(rhandle.getEverywhere(thePlayer), thePlayer);
                            //Checks for Player can be hurt by the Explosion
                            if(thePlayer.getWorldName().equals(block.getWorldName()) && !thePlayer.getMode() && !theZone.getSanctuary() && !thePlayer.isDamageDisabled()){
                                thePlayer.doDamage(1, explodeDamageCalc(thePlayer));
                                //Debugging Message
                                rhandle.log(RLevel.PLAYER_EXPLODE, String.format(debug, thePlayer.getName(), Math.floor(thePlayer.getX()), Math.floor(thePlayer.getY()),
                                                                                        Math.floor(thePlayer.getZ()), thePlayer.getWorldName(), thePlayer.getDimension()));
                            }
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in RPEDThread. (Don't worry Not a major issue)");
        }
    }
    
    private boolean inRange(ICModPlayer thePlayer){
        double baseMinX = block.getX() - 8, baseMinY = block.getY() - 8, baseMinZ = block.getZ() - 8;
        double baseMaxX = block.getX() + 8, baseMaxY = block.getY() + 8, baseMaxZ = block.getZ() + 8;
        double playerX = thePlayer.getX(), playerY = thePlayer.getY(), playerZ = thePlayer.getZ();
        
        return (playerX > baseMinX && playerX < baseMaxX) && (playerY > baseMinY && playerY < baseMaxY) && (playerZ > baseMinZ && playerZ < baseMaxZ);
    }
    
    private int explodeDamageCalc(ICModPlayer thePlayer){
        int damage = 0;
        double point = Math.sqrt(Math.pow(thePlayer.getX() - block.getX(), 2.0D) +
                                 Math.pow(thePlayer.getY() - block.getY(), 2.0D) +
                                 Math.pow(thePlayer.getZ() - block.getZ(), 2.0D));
        if(point <= 3){
            damage = 69;
        }
        else if(point > 3 && point < 4){
            damage = (int)Math.floor(69 % 75);
        }
        else if(point >= 4 && point < 5){
            damage = (int)Math.floor(69 % 50);
        }
        else if(point >= 5 && point < 6){
            damage = (int)Math.floor(69 % 25);
        }
        else if(point >= 6 && point < 7){
            damage = (int)Math.floor(69 % 10);
        }
        else if(point >= 7){
            damage = (int)Math.floor(69 % 5);
        }
        return damage;
    }
}
