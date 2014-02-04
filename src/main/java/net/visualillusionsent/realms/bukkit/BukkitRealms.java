/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.bukkit;

import net.visualillusionsent.minecraft.plugin.VisualIllusionsMinecraftPlugin;
import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPlugin;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.RealmsTranslate;
import net.visualillusionsent.realms.commands.RealmsCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author Jason (darkdiplomat)
 */
public class BukkitRealms extends VisualIllusionsBukkitPlugin {

    private RealmsBase base;

    static {
        // Check for VIUtils/JDOM2, download as necessary
        Manifest mf = null;
        try {
            mf = new JarFile(BukkitRealms.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getManifest();
        }
        catch (IOException ex) {
            // NullPointerException will happen anyways
        }
        String viutils_version = mf.getMainAttributes().getValue("VIUtils-Version");
        String vi_url = MessageFormat.format("http://repo2.visualillusionsent.net/repository/public/net/visualillusionsent/viutils/{0}/viutils-{0}.jar", viutils_version);
        String jdom_version = mf.getMainAttributes().getValue("JDOM2-Version");
        String jdom_url = MessageFormat.format("http://repo1.maven.org/maven2/org/jdom/jdom2/{0}/jdom2-{0}.jar", jdom_version);
        try {
            VisualIllusionsMinecraftPlugin.getLibrary("Realms", "viutils", viutils_version, new URL(vi_url), Bukkit.getLogger());
            VisualIllusionsMinecraftPlugin.getLibrary("Realms", "jdom2", jdom_version, new URL(jdom_url), Bukkit.getLogger());
        }
        catch (MalformedURLException e) {
            // the URLs are correct
        }
        //
    }

    @Override
    public final void onEnable() {
        super.onEnable();

        base = new RealmsBase(new Bukkit_Server(this, getServer(), getLogger()));
        RealmsCommandHandler.initialize();
        RealmsTranslate.initialize();
        new Realms_BukkitListener(this);
        getCommand("realms").setExecutor(new RealmsBukkitCommandExecutor(this));
    }

    @Override
    public final void onDisable() {
        HandlerList.unregisterAll(this);
        if (RealmsBase.isLoaded()) {
            base.terminate();
        }
    }

    /* VIMCPlugin */
    @Override
    public Logger getPluginLogger() {
        return getLogger();
    }
    //
}
