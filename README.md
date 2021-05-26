# DAISY Pipeline 1 project
The DAISY Pipeline is a cross-platform, open source framework for DTB-related document transformations. It provides a comprehensive solution for converting text documents into accessible formats for people with print disabilities.


# pipeline lite build (windows)

For the pipeline lite, you will need to build the GUI libs from the `https://sourceforge.net/projects/daisymfcgui/` repository with eclipse and java 8 (the build break with newer java version).
You will also need the launch4j tool to build an executable launcher, and LAME encoder installed.

For each project of the daisymfgui repo, build the classes and export them as a jar named as `<projectname>_<datestamp>.jar` with the manifest.
Also build the pipeline launcher with launch using the launch4j setting file in the org.daisy.pipeline.lite project folder.

Put the jars and the launcher in a 'libs' folder somewhere in your disk outside the project.

You will also need to retrieve the following libraries jar required and put them in the previous libs folder :
- javax.xml.stream_*
- org.eclipse.core.commands_*
- org.eclipse.equinox.common_*
- org.eclipse.jface_*
- org.eclipse.swt.win32*
- org.eclipse.swt_*

i would also recommend putting in this libs folder a copy of the `org.daisy.pipeline/src/pipeline.user.properties` and rename it `pipeline.user.win.properties`.
You will need at minimum to change the lame path in this configuration to be `ext\\lame.exe`
Here is a sample configuration you can copy if needed :

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>		
	<comment>
		This is one of the two configuration files of the Daisy Pipeline. 
		This file (pipeline.user.properties) contains properties that are likely to need access by users.
		The other file (pipeline.properties) contains properties that are unlikely to need access by users.
		The properties registered here are put in System.properties. 
	</comment>			
	<entry key="pipeline.tempDir"></entry>	
	<entry key="pipeline.lame.path">ext\\lame.exe</entry>	
</properties>
```

In the `build-addin.xml` file, search and change the following line by replacing the `value` attributes content by the lib folder path and your LAME encoder install path :


```xml
<property name="lite.lib.home" value="C:\path\to\libsfolder"/>
<property name="launchers.home" value="C:\path\to\libsfolder"/>
<property name="lame.home" value="C:\Program Files\LAME"/>
```

