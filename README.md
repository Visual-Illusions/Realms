# Realms 7 #
### Polygonal Hierarchical Area Ownership Plugin for the CanaryMod and Bukkit Minecraft Server Modifications ###
#### &copy; Copyright 2012 - 2013 Visual Illusions Entertainment ####
#### Licensed under the GNU General Public License v3 ####
#### Realms originally developed by Impact and durron597 (versions before v3) ####

## About ##
See the [Realms Wiki Page](http://wiki.visualillusionsent.net/Realms "wiki_realms") for more information

## Bug Reporting ##
Please use the [GitHub issue tracker](https://github.com/Visual-Illusions/Realms/issues "issues") associated with this repository.<br>
Be sure to include as much information about the issue as possible, including Server Mod info

## Compiling ##
Create a new project in the IDE of your choice<br>
Change the source folder to be src/main/java<br>
Add a current CanaryMod.jar, minecraft_servero.jar (Canary generated), Bukkit API jar, sqlite.jar, viutils-1.0.1.jar, and jdom2.jar to the build path  (verify the names are the same, renaming may be required)<br>

Maven:<br>
Place CanaryMod.jar and minecraft_servero.jar in a folder named 'lib' in your main project directory<br>
Run the pom.xml<br>

Ant:<br>
Place CanaryMod.jar, minecraft_servero.jar, Bukkit API jar, sqlite.jar, viutils-1.0.1.jar, and jdom2.jar in a folder named 'lib' in your main project directory<br>
Right click the realms_ant_build.xml and select Run As -> Ant Build<br>
Files will be located in a dist folder in your project area<br>

Normal:<br>
Add a current CanaryMod.jar, Bukkit API jar, sqlite.jar, viutils-1.0.1.jar, and jdom2.jar to the build path  (verify the names are the same, renaming may be required)<br>
You may also need a reference to minecraft_servero.jar from CanaryMod for compliation.<br>
Export with the provide MANIFEST.MF file!<br>

You can find VIUtils v1.0.1 from the [Visual Illusions Repository](http://repo.visualillusionsent.net/net/visualillusionsent/viutils/1.0.1/viutils-1.0.1.jar "viutils-download")<br>
JDOM2 can be found by visiting [jdom.org](http://www.jdom.org/downloads/index.html "jdom")<br>
SQLite3-JDBC can be found by visiting [xerial.org](http://www.xerial.org/maven/repository/artifact/org/xerial/sqlite-jdbc/3.7.2/ "sqlite-jdbc")<br>
Bukkit API Jars can be found at [dl.bukkit.org](http://dl.bukkit.org/downloads/bukkit/ "bukkit-api") (Requires bukkit-1.4.5-R1.0.jar or greater [untested with anything below 1.4.5])<br>
CanaryMod can be found at [CanaryMod.net](http://www.canarymod.net/download "canary")  (Requires Crow 5.7.11 or greater)

## License ##
Realms v7
&copy; Copyright 2012 - 2013 Visual Illusions Entertainment

Author: Jason Jones (darkdiplomat) <darkdiplomat@visualillusionsent.net>

Realms is free software: you can redistribute it and/or modify<br>
it under the terms of the GNU General Public License as published by<br>
the Free Software Foundation, either version 3 of the License,<br>
or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; <br>
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.<br>
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.<br>
If not, see [http://www.gnu.org/licenses/gpl.html](http://www.gnu.org/licenses/gpl.html "gpl")
