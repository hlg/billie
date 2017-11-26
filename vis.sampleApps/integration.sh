# The content for the data directory can be found
# at http://helgatauscher.de/billie/data
# or http://helgatauscher.de/billie/data.zip

# ------------------------------
# Precompiled configurations
# ------------------------------

./configrunner.sh IFC_3D data/carport.ifc
./configrunner.sh IFC_3D data/EFH.ifc
./configrunner.sh IFC_3D data/airport.ifc # takes long to load
./configrunner.sh IFC_3D data/airport_part.ifc
./configrunner.sh IFC_3D data/highrise.ifc

./configrunner.sh IFC_3D_AXONOMETRIC data/carport.ifc
./configrunner.sh IFC_3D_AXONOMETRIC data/EFH.ifc
./configrunner.sh IFC_3D_AXONOMETRIC data/airport.ifc # takes long to load
./configrunner.sh IFC_3D_AXONOMETRIC data/airport_part.ifc
./configrunner.sh IFC_3D_AXONOMETRIC data/highrise.ifc

./configrunner.sh IFC_3D_INTERACTIVE data/carport.ifc
./configrunner.sh IFC_3D_INTERACTIVE data/EFH.ifc
./configrunner.sh IFC_3D_INTERACTIVE data/airport.ifc # takes long to load
./configrunner.sh IFC_3D_INTERACTIVE data/airport_part.ifc
./configrunner.sh IFC_3D_INTERACTIVE data/highrise.ifc

./configrunner.sh IFCSPACE_3D data/EFH.ifc
# no spaces in carport, airport, and highrise

./configrunner.sh IFC_4D data/carport.zip
./configrunner.sh IFC_4D data/EFH_activity.zip
./configrunner.sh IFC_4D data/airport.zip # uses a lot of memory
# IFC_4D highrise_web: multiple activity namespaces not handled

./configrunner.sh IFCGAEBQTO_3D data/carport.zip
./configrunner.sh IFCGAEBQTO_3D data/EFH_quantity.zip
./configrunner.sh IFCGAEBQTO_3D data/airport.zip # uses a lot of memory
# IFCGAEBQTO_3D highrise_web: multiple GAEB and QTO namespaces not handled

./configrunner.sh GAEB_BARCHART data/carport.x81
./configrunner.sh GAEB_BARCHART data/EFH.x86
./configrunner.sh GAEB_BARCHART data/airport.x81
./configrunner.sh GAEB_BARCHART data/airport.x84
./configrunner.sh GAEB_BARCHART data/highrise.x81

./configrunner.sh GANTT data/carport_activity.xml
./configrunner.sh GANTT data/EFH_activity.xml
./configrunner.sh GANTT data/highrise_activity.xml
./configrunner.sh GANTT data/highrise_activity_detailed.xml
./configrunner.sh ICAL_GANTT data/EFH.ics
./configrunner.sh ICAL_GANTT data/EFH_compressed.ics
./configrunner.sh PROGRESS_GANTT data/airport ./configrunner.sh IFC_REPORTS_4D data/airport.zip # uses a lot of memory
# no progress data for carport, efh, and highrise 

./configrunner.sh IFC_ICYCLE data/carport.ifc 
./configrunner.sh IFC_ICYCLE data/EFH.ifc 
./configrunner.sh IFC_ICYCLE data/airport.ifc # takes long to load
./configrunner.sh IFC_ICYCLE data/airport_part.ifc
./configrunner.sh IFC_ICYCLE data/highrise.ifc

./configrunner.sh GAEB_ICYCLE data/carport.x81
./configrunner.sh GEAB_ICYCLE data/EFH.x86
./configrunner.sh GAEB_ICYCLE data/airport.x81
./configrunner.sh GAEB_ICYCLE data/airport.x84 
./configrunner.sh GAEB_ICYCLE data/highrise.x81

./configrunner.sh LINKS_HEB data/carport # unzipped
./configrunner.sh LINKS_HEB data/EFH_quantity  # unzipped
./configrunner.sh LINKS_HEB data/airport # unzipped
# LINKS_HEB highrise_web: multiple GAEB and QTO namespaces not handled


# ------------------------------
# BISL DSL configurations
# ------------------------------

./dslrunner.sh bisl/ifc_3d.vis data/carport.ifc
./dslrunner.sh bisl/ifc_3d.vis data/EFH.ifc
./dslrunner.sh bisl/ifc_3d.vis data/airport.ifc
./dslrunner.sh bisl/ifc_3d.vis data/airport_part.ifc
./dslrunner.sh bisl/ifc_3d.vis data/highrise.ifc

./dslrunner.sh bisl/ifc_space_3d.vis data/carport.ifc
# no spaces in carport, airport, and highrise

./dslrunner.sh bisl/ifc_sched_4d.vis data/carport.ifc
./dslrunner.sh bisl/ifc_sched_4d.vis data/EFH.ifc
./dslrunner.sh bisl/ifc_sched_4d.vis data/airport.ifc
./dslrunner.sh bisl/ifc_sched_4d.vis data/airport_part.ifc
./dslrunner.sh bisl/ifc_sched_4d.vis data/highrise.ifc


