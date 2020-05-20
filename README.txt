Notes on opening and editing source files
-----
- to open source files in Eclipse import the ecips project from inside Eclipse
- if there are library errors
     -leftclick folder -> Build path -> configure Build Path -> add the 5 jar files in the UCanAccess-4.0.4-bin file

Notes on useing program
-----
-the file SpreadsheetDB.accdb must be in the same file as the runnable jar file in order for the program to work correctly
- when exporting put refereances libraries in a sub directory

Notes on version control
-----
- when a new version is made copy the files to a different dorectory and rename working parent directory to new version
- if the project file is renamed the eclipse project needs to be deleted and reimported

file structure
------
activityCalculator_version
-metadata
-referenced libraries
-project file (ActivityCalculator)
|-bin
|-settings
|-src (java files)
|-data base
|-readme