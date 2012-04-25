package net.visualillusionsent.realms.io;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Restricted
 *      - Called for players in a restricted zone and not authed
 * 
 * @author darkdiplomat
 */
public class RestrictionDamager implements Runnable{
    private RHandle rhandle;
        
    /**
     * Class Constructor
     * 
     * @param Realm
     */
    public RestrictionDamager (RHandle rhandle) {
        this.rhandle = rhandle;
    }
    
    /**
     * Realms Player Restricted Damage Run
     */
    public void run() {
        try{
            /*Player Lists*/
            List<ICModPlayer> restricted = ZoneLists.getInRestricted();
                
            /*Check Player List*/
            if (!restricted.isEmpty()){
                synchronized(restricted){
                    for(ICModPlayer thePlayer : restricted){
                        Zone zone = ZoneLists.getZone(rhandle.getEverywhere(thePlayer), thePlayer);
                        //Double Checks for Player can be hurt by the zone
                        if(!zone.permissionCheck(thePlayer, Permission.PermType.AUTHED) && !thePlayer.getMode() && !thePlayer.isDamageDisabled() && zone.getRestricted()){
                            //check that we can kill the player
                            if(!RealmsProps.getRestrictKills() && thePlayer.getHealth()-1 <= 0){
                                continue;
                            }
                            //Hurt Player
                            thePlayer.doDamage(1);
                            
                            //Did we kill the player?
                            if(thePlayer.getHealth() <= 0){
                                //Yep, drop their inventory
                                thePlayer.dropInventory();
                            }
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in RestrictedDamager. (Don't worry Not a major issue)");
        }
    }
}
