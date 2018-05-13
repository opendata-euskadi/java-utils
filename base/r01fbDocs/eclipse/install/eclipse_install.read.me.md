Eclipse install
===============
## [1]: Create the file system structure
```
		/ (d:\ in windows or /develop in linux)
			+ eclipse
				+ instances
					+ [instance_name]
				+ ivy_libs
				+ local_libs
				+ workspaces
					+ master_[instance_name]
```

## [2]: download the [eclipse IDE for Java Developers] from http://www.eclipse.org/downloads/eclipse-packages/

> BEWARE!! do NOT download the [eclipse IDE for Java EE Developers]

## [3]: Extract the contents of the [eclipse] folder inside the previously downloaded eclipse ZIP to the `/eclipse/instances/[instance_name]`

## [4]: Copy the `/eclipse/instances/[instance_name]/eclipse.ini` to eclipse.ini.original

## [5]: Edit the `/eclipse/instances/[instance_name]/eclipse.ini` file and set this content:

> BEWARE!!!

a) check that the org.eclipse.equinox.launcher versions still the SAME as those in the eclipse.ini.original file

b) replace [instance_name] with it's real value

```console
			-clean
			-startup
			plugins/org.eclipse.equinox.launcher_1.3.200.v20160318-1642.jar
			--launcher.library
			plugins/org.eclipse.equinox.launcher.win32.win32.x86_64_1.1.400.v20160518-1444
			-product
			org.eclipse.epp.package.java.product
			--launcher.defaultAction
			openFile
			--launcher.XXMaxPermSize
			256M
			-showsplash
			org.eclipse.platform
			--launcher.defaultAction
			openFile
			--launcher.appendVmargs

			# JDK 1.8 <<<<<<<< USE JDK8 if runninig OEPE (Oracle Enterprise Pack)
			-vm
			d:/java/jdk8/jre/bin/server/jvm.dll
			-vmargs

			# JDK9: see  https://wiki.eclipse.org/Configure_Eclipse_for_Java_9
			--launcher.appendVmargs
			-vm
			d:/java/jdk9/bin/server/jvm.dll
			-vmargs
			--add-modules=ALL-SYSTEM


			# see [Runtime Options] http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/reference/misc/index.html
			# see http://stackoverflow.com/questions/316265/how-can-you-speed-up-eclipse/316535#316535
			-Dosgi.requiredJavaVersion=1.8
			-XX:+UseG1GC
			-XX:+UseStringDeduplication
			-Dosgi.requiredJavaVersion=1.8
			-Dosgi.clean=true
			-Duser.language=en
			-Duser.country=US
			-Dhelp.lucene.tokenizer=standard
			-javaagent:lombok.jar
			-Xbootclasspath/a:lombok.jar
			-Xms256m
			-Xmx1024m
			-Xverify:none
	```

## [6]: Create a shortcut to the /eclipse/instances/[instance_name]/eclipse.exe file

## [7]: Change the workspace to /eclipse/workspaces/master_[instance_name]

## [8]: Install plugins:

a) **AJDT: AspectJ Development Tools** > 	http://download.eclipse.org/tools/ajdt/47/dev/update

b) **IvyDE** > https://builds.apache.org/job/IvyDE-updatesite/lastSuccessfulBuild/artifact/trunk/build/   or    http://www.apache.org/dist/ant/ivyde/updatesite

```
	Window > Preferences > Ivy
				Classpath container:
					[X] Resolve dependencies in the workspace
					[X] Resolve dependencies transitively
				Settings
					Ivy user dir: D:\eclipse\ivy_libs
```
c) **Eclipse WTP tools** (from eclipse update site)

```
		  Web, XML, Java EE and OSGi Enterprise Development
			[X]	Eclipse Faceted Project Framework
			[X] Eclipse Faceted Project Framework JDT Enablement
			[X] Java EE developer tools
			[X] Eclipse Java Web Developer Tools
			[X] Eclipse Web Developer Tools
			[X] Eclipse XSL Developer Tools
			[X] JavaScript Development Tools
			[X] JavaScript Development Tools Chromium/V8 Remote Debugger
			[X] JST Server Adapters
			[X] JST Server Adapters Extensions
			[X] JST Server UI
			[X] WST Server Adapters
```

d) **Colaboration Tools: SVN**

```
					[X] Subversive SVN Connectors
					[X] Subversive SVN JDT Ignore Extensions (Optional)
					[X] Subversive SVN Team Provider
```

e) **[Polarion SVN connectors]**

Goto `[Window]->[Preferences]->[Team]->[SVN]->[Connectors]`

Select AT LEAST [svnkit]

**NOTE:** If Polarion SVN connectors cannot be downloaded:

	1.- Goto https://polarion.plm.automation.siemens.com/products/svn/subversive/download

	2.- Select a polarion svn connector repository
							ie: http://community.polarion.com/projects/subversive/download/eclipse/6.0/update-site/?__hstc=2015854.4bd76f2b8b2bce1c569b50fed7cf3b42.1506603368284.1506603368284.1506603368284.1&__hssc=2015854.1.1506603368285&__hsfp=3974841547
	3.- Goto [Help] > [Install New Software]

	4.- Add a new repository and install

 If this DOES NOT WORKS, try to install from a ZIP file downloaded from:
	http://community.polarion.com/projects/subversive/download/eclipse/6.0/builds/

 If this DOES NOT EVEN WORKS try:

					1.- DELETE all org.polarion zip files from d:\eclipse\instances\master_photonM2\plugins
					2.- DELETE all polarion ARTIFACTS from d:\eclipse\instances\master_photonM2\artifacts.xml

f) **[AnyEdit Tools]** either using the [eclipse marketplace] or from the update site at: http://andrei.gmxhome.de/eclipse/

## [9]: Configure plugins

		AnyEdit Tools > Change preference at [General] > [Editor] > [AnyEditTools] > Remove Trailing spaces (DISABLE)
