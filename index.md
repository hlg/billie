---
title: Billie. A BIM stylesheet engine
author: Helga Tauscher, TU Dresden, [CIB](http://tu-dresden.de/bau/cib)
nocite: |
  @Tauscher2011a, @Tauscher2011, @Tauscher2012, @Tauscher2013, @Tauscher2013a, @Tauscher2014a, @Tauscher2015, @Tauscher2015b, @Tauscher2016, @Tauscher2016a, @Tauscher2017
...

Billie is a mapping engine dedicated to generate visual representations from building information models based on a visualization description.

Installation
==============

Download and unpack the archive of the appropriate release from [github](http://github.com/hlg/billie/releases/). The alpha releases contain all modules plus two sample applications. The final version will be released in smaller specific packages. 

Usage
============

There are two commandline applications to create visualizations: one uses precompiled configurations, the other loads configurations from visualization description files. Both commandline applications can be called with a script which comes as a Windows batch file and as a Unix shell script.

Configuration runner
--------------------

The configuration runner (`de.tudresden.cib.vis.sampleApps.ConfigurationRunner`) loads a selected precompiled visualization configuration CONFIGNAME and applies it to a building information model loaded from BIMFILE.

    configurationrunner.bat [CONFIGNAME [BIMFILE]]
    ./configurationrunner.sh [CONFIGNAME [BIMFILE]]

If no CONFIGNAME is given, all available configurations are listed. If no BIMFILE is given, a file selection dialog prompts for the respective input file or folder.

The sample configurations included in the alpha release are listed and described in section [Sample configurations](#sample-configurations). For the creation of custom configurations, see section [Precompiled Configurations](#precompiled-configurations). 


DSL runner
----------------

de.tudresden.cib.vis.DSL.VisDSLRunner

    dslrunner.bat CONFIGFILE [BIMFILE]
    ./dslrunner.sh CONFIGFILE [BIMFILE]

If no BIMFILE is given, a file selection dialog prompts for the respective input file or folder.

The DSL version of the visualization description is only implemented as a very rough sketch in the alpha release and does not yet reflect the functionality of the precompiled visualization descriptions. Only simple mapping without animation and interaction is possible as demonstrated with `ifc_3d.vis`. For the description of the  visualization DSL see section [DSL](#dsl). 



Precompiled Configurations
===========================

In order to create custom precompiled configurations, the class `de.tudresden.cib.vis.mapping.Configuration` has to be instantiated. It has a generified class signature to allow for the compile time compability check of the data accessor, mapper and scene graph. Due to type erasure, generics are useless in the case of configurations specified by a DSL (since this is only loaded at runtime), and thus the generic class signature should be replaced by some other mechanism in the future.

The class signature `Configuration<E, C>` is generified with the type of the data elements (`E`) to be mapped to visualization objects and the type of the condition (`C`) used to specify filter specifications. The type of the condition depends on the filter library used. The default filter library uses a condition oject `Condition<E>`. Other libraries could use strings or custom types to specify conditions.
  
A configuration holds the following entities: statistical functions to extract values by folding, globals to calculate and save general values, and mappings. These can be added by using the respective functions `addStatistics(String name, Folding function)`, `addGlobal(String name, Preprocessing function)` and `addMapping(Condition  condition, PropertyMap mapping)`.

Examples of precompiled configurations can be found in the package [de.tudresden.cib.vis.configurations](https://github.com/hlg/billie/tree/master/vis.configurations/src/de/tudresden/cib/vis/configurations). They are accessible through the configuration runner described [earlier](#configuration-runner).


Statistics and globals
----------------------
__TODO__

Mappings
--------
A mapping is specified by giving a condition and a property map. The condition is then later used by a filter library to determine a set of objects to apply the property map. The default filter liberary uses a `Condition` object with a `matches` method returning a boolean value that indicates whether a given object is to be considered for mapping. The `PropertyMap` is generified with the type of the source data elements and the type of the target visualization elements. Property maps must implement the abstract `configure` method where the properties of the visualization elements can be set depending on properties of the source element and additional values from data preprocessing. Inside the `configure` method the source object can be accessed as `data` and the target object as `graphObject`.

TODO: example `addMapping...`


Updating mappings
-----------------

Updating mappings change the properties of visualization objects created in advance. They can either be triggered by the advance of time to a specific point or by an event generated by user interaction. Accordingly, there are two different method signatures to add updates: `addChange(int time, de.tudresden.cib.vis.scene.Change<T> change)` and `addChange(Event event, de.tudresden.cib.vis.scene.Change<T> change)`.

A `Change` is similar to a `PropertyMap` in that it has to implement a `configure` method in order to set properties of the visualization object. However, it does not create the object in question, but acts on an already existing object. The association of a change and the visualization object it operates on is currently created by defining the changes together with the initial visualization object setup in the property map.

__TODO__: example `addChange...`


TODO
----
* complex configurations
* autodetecting config runner


DSL
==============

The implementation status of the DSL is currently only partial. The documentation will grow as subsequent features are implemented.

The DSL is based on Groovy, which is suitable for DSLs due to its concise character and features such as closures and functional programming constructs. Groovy is a language for the JVM. It extends Java and all Java constructs can be used. Further documentation can be found on [groovy-lang.org](http://groovy-lang.org/documentation.html). A production DSL would restrict the usable language constructs, but in favour of academic liberty no such restrictions are built into the prototypical DSL implementation.


Basic mapping
-------------

A basic mapping rule is specified by giving the types of the source and target object, a condition and a mapping as follows:

    vt.rule(EngineEObject, Polyeder){
      condition {
        data.object instanceof IfcBuildingElement
      }
      initial {
        graphObject.vertizes = data.geometry.vertizes
        graphObject.normals = data.geometry.normals
        graphObject.indizes = data.geometry.indizes
      }
    }

The closure following `condition` is evaluated and should return true if the mapping should be applied to the object, false otherwise. The closure following the keyword `initial` is evaluated for all matching data objects, after the creation of a respective visualization object.


__TODO__

* updates, animated and event controlled
* complex config
* examples


Sample Configurations
=====================

* IFC_3D: simple visualization of the 3D geometry of building elements in an IFC file, navigable with perspective projection
* IFC_3D_AXONOMETRIC: same as IFC_3D, but with axonometric projection and rotated view
* IFC_3D_INTERACTIVE: same as IFC_3D, but with an additional mini toolbar to select and hightlight objects
* IFCSPACE_3D: same as IFC_3D, but visualizing spaces instead of building elements
* GAEB_BARCHART: simple barchart visualizing the costs from a [GAEB](http://www.gaeb.de/en/products/gaeb-data-exchange/) file, with labels
* IFC_4D: animated visualization of the construction proces of a building from multimodel data containing a schedule
* IFCGAEBQTO_3D: 3D visualization with a colour scale based on the cost information in a multimodel with a GAEB file


Sample Files
===============

We are providing sample files from four different projects:

* _Carport_ is a very simple synthetic test file with four columns and a slab.
* _EFH_ is a two-storey single family house.
* _Highrise_ is an office tower
* _Airport_ is an airport complex

For each project there are two types of input files:

* single BIM files in various formats such as IFC or GAEB
* multi models combining multiple single BIM files in a container

Sometimes the same contents can be expressed in different formats, e.g. schedule data in a multi model might either be present in the IFC, or as an ICAL, or as a custom XML file. Also, multi model containers exist in different versions --- as specific mefisto containers or as more general MMAA containers. Since Billie does not provide abstraction on the BIM input side, the different input formats require different visualization configurations. However, the sample files provided here cover only one specific of multiple potentially equivalent formats. Note that this has the effect, that for some visualization configurations there is no sample file provided, namely those which have an equivalent configuration with an equivalent input format.

The sample files with their appropriate visualization configurations for their respective format are given below. There is also a script file containing a list of all combinations in the [Github repository](https://github.com/hlg/billie/blob/master/vis.sampleApps/integration.sh) and a [zip archive](http://helgatauscher.de/billie/data.zip) containing all data files neatly packaged. 

Carport
-------
This sample project is a very simple synthetic project made for teaching purposes at [CIB](http://tu-dresden.de/bau/cib) consisting of a slab and four columns.

* [carport.ifc](http://helgatauscher.de/billie/data/carport.ifc): IFC_3D, IFC_3D_AXONOMETRIC, IFC_3D_INTERACTIVE, IFC_ICYCLE
* [carport.x81](http://helgatauscher.de/billie/data/carport.x81): GAEB_BARCHART, GAEB_ICYCLE
* [carport_activity.xml](http://helgatauscher.de/billie/data/carport_activity.xml): GANTT
* [carport.zip](http://helgatauscher.de/billie/data/carport.zip): IFC_4D, IFCGAEBQTO_3D, LINKS_HEB

EFH
----
This sample project features a two-storey single family house. It is based on demo data from the research project [eWorkBau](http://ework-bau.de), the support of the German Federal Ministry of Education and Research [BMBF](https://www.bmbf.de/en/) (grant no. 01PF07045A) is gratefully acknowledged.

* [EFH.ifc](http://helgatauscher.de/billie/data/EFH.ifc): IFC_3D, IFC_3D_AXONOMETRIC, IFC_3D_INTERACTIVE, IFCSPACE_3D, IFC_ICYCLE
* [EFH.x86](http://helgatauscher.de/billie/data/EFH.x86): GAEB_BARCHART, GAEB_ICYCLE
* [EFH_activity.xml](http://helgatauscher.de/billie/data/EFH_activity.xml): GANTT
* [EFH.ics](http://helgatauscher.de/billie/data/EFH.ics), [EFH_compressed.ics](http://helgatauscher.de/billie/data/EFH_compressed.ics): ICAL_GANTT
* [EFH_activity.zip](http://helgatauscher.de/billie/data/EFH_activity.zip): IFC_4D
* [EFH_quantity.zip](http://helgatauscher.de/billie/data/EFH_quantity.zip): IFCGAEBQTO_3D, LINKS_HEB

Highrise
-------------
This sample project features a multistorey office building. Is is based on demo data from the research project [mefisto](http://mefisto-bau.de), the support of the German Federal Ministry of Education and Research [BMBF](https://www.bmbf.de/en/) (grant no. 01LA09001) is gratefully acknowledged.

* [highrise.ifc](http://helgatauscher.de/billie/data/highrise.ifc): IFC_3D, IFC_3D_AXONOMETRIC, IFC_3D_INTERACTIVE, IFC_ICYCLE
* [highrise.x81](http://helgatauscher.de/billie/data/highrise.x81): GAEB_BARCHART, GAEB_ICYCLE
* [highrise_activity.xml](http://helgatauscher.de/billie/data/highrise_activity.xml), [highrise_activity_detailed.xml](http://helgatauscher.de/billie/data/highrise_activity_detailed.xml): GANTT

Airport
----------
This sample project features a multistorey office building. Is is based on demo data from the research project [mefisto](http://mefisto-bau.de), the support of the German Federal Ministry of Education and Research [BMBF](https://www.bmbf.de/en/) (grant no. 01LA09001) is gratefully acknowledged.

* [airport.ifc](http://helgatauscher.de/billie/data/airport.ifc), [airport_part.ifc](http://helgatauscher.de/billie/data/airport_part.ifc): IFC_3D, IFC_3D_AXONOMETRIC, IFC_3D_INTERACTIVE, IFC_ICYCLE
* [airport.x81](http://helgatauscher.de/billie/data/airport.x81), [airport_part.x84](http://helgatauscher.de/billie/data/airport.x84): GAEB_BARCHART, GAEB_ICYCLE
* [airport.zip](http://helgatauscher.de/billie/data/airport.zip): IFC_4D, IFCGAEBQTO_3D, LINKS_HEB



Embedding
==============

* API
* with DSL
* with fixed configurations

Extending
==============

* data access
* filter libraries
* visualization environments

Contact and support
====================

For discussion, questions, and support please subscribe to [billie@listserv.dfn.de](http://www.listserv.dfn.de/cgi-bin/wa?SUBED1=billie&A=1).

Theoretical background
=========================

The prototype is the result of the research carried out for my thesis with the working title "Configurable nD-visualization for Building Information Models".

In the architecture and construction sector, there is a strong tradition and a lot of competence in working with visual representations. With the rise of digital modelling, visual representations were decoupled from the information, which is now only an abstract mystic lump of data to most architects and engineers. Since visual representations are generated on the fly in dedicated software packages, architects and engineers have very few options to control visual representations. The work is based on the hypothesis that this is a conflict which constricts the creative work of architects and engineers and that the full potential of BIM can only be unlocked with accessible and configurable visual representations.

I have talked and written about the idea from different perspectives and about potential use cases at conferences during the previous years. You can find details in the following articles from conference proceedings and journals and in my thesis ([printed](https://tredition.de/autoren/helga-tauscher-yb5823/configurable-nd-visualization-for-complex-building-information-models-50551/) or [online](http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-228894)).


