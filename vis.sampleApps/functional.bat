REM The content for the data directory can be found
REM at http://helgatauscher.de/billie/data
REM or http://helgatauscher.de/billie/data.zip

REM ------------------------------
REM Precompiled configurations
REM ------------------------------

configrunner.bat IFC_3D data\carport.ifc
configrunner.bat IFC_3D data\EFH.ifc
configrunner.bat IFC_3D data\airport.ifc 
REM takes long to load
configrunner.bat IFC_3D data\airport_part.ifc
configrunner.bat IFC_3D data\highrise.ifc

configrunner.sh IFC_3D_AXONOMETRIC data\carport.ifc
configrunner.sh IFC_3D_AXONOMETRIC data\EFH.ifc
configrunner.sh IFC_3D_AXONOMETRIC data\airport.ifc 
REM takes long to load
configrunner.sh IFC_3D_AXONOMETRIC data\airport_part.ifc
configrunner.sh IFC_3D_AXONOMETRIC data\highrise.ifc

configrunner.bat IFC_3D_INTERACTIVE data\carport.ifc
configrunner.bat IFC_3D_INTERACTIVE data\EFH.ifc
configrunner.bat IFC_3D_INTERACTIVE data\airport.ifc 
REM takes long to load
configrunner.bat IFC_3D_INTERACTIVE data\airport_part.ifc
configrunner.bat IFC_3D_INTERACTIVE data\highrise.ifc

configrunner.bat IFCSPACE_3D data\EFH.ifc
REM no spaces in carport, airport, and highrise

configrunner.bat IFC_4D data\carport.zip
configrunner.bat IFC_4D data\EFH_activity.zip
configrunner.bat IFC_4D data\airport.zip 
REM uses a lot of memory
REM IFC_4D highrise_web: multiple activity namespaces not handled

configrunner.bat IFCGAEBQTO_3D data\carport.zip
configrunner.bat IFCGAEBQTO_3D data\EFH_quantity.zip
configrunner.bat IFCGAEBQTO_3D data\airport.zip 
REM uses a lot of memory
REM IFCGAEBQTO_3D highrise_web: multiple GAEB and QTO namespaces not handled

configrunner.bat GAEB_BARCHART data\carport.x81
configrunner.bat GAEB_BARCHART data\EFH.x86
configrunner.bat GAEB_BARCHART data\airport.x81
configrunner.bat GAEB_BARCHART data\airport.x84
configrunner.bat GAEB_BARCHART data\highrise.x81

configrunner.bat GANTT data\carport_activity.xml
configrunner.bat GANTT data\EFH_activity.xml
configrunner.bat GANTT data\highrise_activity.xml
configrunner.bat GANTT data\highrise_activity_detailed.xml
configrunner.bat ICAL_GANTT data\EFH.ics
configrunner.bat ICAL_GANTT data\EFH_compressed.ics
configrunner.bat PROGRESS_GANTT data\airport configrunner.bat IFC_REPORTS_4D data\airport.zip 
REM uses a lot of memory
REM no progress data for carport, efh, and highrise 

configrunner.bat IFC_ICYCLE data\carport.ifc 
configrunner.bat IFC_ICYCLE data\EFH.ifc 
configrunner.bat IFC_ICYCLE data\airport.ifc 
REM takes long to load
configrunner.bat IFC_ICYCLE data\airport_part.ifc
configrunner.bat IFC_ICYCLE data\highrise.ifc

configrunner.bat GAEB_ICYCLE data\carport.x81
configrunner.bat GEAB_ICYCLE data\EFH.x86
configrunner.bat GAEB_ICYCLE data\airport.x81
configrunner.bat GAEB_ICYCLE data\airport.x84 
configrunner.bat GAEB_ICYCLE data\highrise.x81

configrunner.bat LINKS_HEB data\carport 
REM unzipped
configrunner.bat LINKS_HEB data\EFH_quantity  
REM unzipped
configrunner.bat LINKS_HEB data\airport 
REM unzipped
REM LINKS_HEB highrise_web: multiple GAEB and QTO namespaces not handled


REM ------------------------------
REM BISL DSL configurations
REM ------------------------------

dslrunner.bat bisl\ifc_3d.vis data\carport.ifc
dslrunner.bat bisl\ifc_3d.vis data\EFH.ifc
dslrunner.bat bisl\ifc_3d.vis data\airport.ifc
dslrunner.bat bisl\ifc_3d.vis data\airport_part.ifc
dslrunner.bat bisl\ifc_3d.vis data\highrise.ifc

dslrunner.bat bisl\ifc_space_3d.vis data\carport.ifc
REM no spaces in carport, airport, and highrise

dslrunner.bat bisl\ifc_sched_4d.vis data\carport.ifc
dslrunner.bat bisl\ifc_sched_4d.vis data\EFH.ifc
dslrunner.bat bisl\ifc_sched_4d.vis data\airport.ifc
dslrunner.bat bisl\ifc_sched_4d.vis data\airport_part.ifc
dslrunner.bat bisl\ifc_sched_4d.vis data\highrise.ifc


