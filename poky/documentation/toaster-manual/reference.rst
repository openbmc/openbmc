.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

**********************
Concepts and Reference
**********************

In order to configure and use Toaster, you should understand some
concepts and have some basic command reference material available. This
final chapter provides conceptual information on layer sources,
releases, and JSON configuration files. Also provided is a quick look at
some useful ``manage.py`` commands that are Toaster-specific.
Information on ``manage.py`` commands does exist across the Web and the
information in this manual by no means attempts to provide a command
comprehensive reference.

Layer Source
============

In general, a "layer source" is a source of information about existing
layers. In particular, we are concerned with layers that you can use
with the Yocto Project and Toaster. This chapter describes a particular
type of layer source called a "layer index."

A layer index is a web application that contains information about a set
of custom layers. A good example of an existing layer index is the
OpenEmbedded Layer Index. A public instance of this layer index exists
at :oe_layerindex:`/`. You can find the code for this
layer index's web application at :yocto_git:`/layerindex-web/`.

When you tie a layer source into Toaster, it can query the layer source
through a
`REST <https://en.wikipedia.org/wiki/Representational_state_transfer>`__
API, store the information about the layers in the Toaster database, and
then show the information to users. Users are then able to view that
information and build layers from Toaster itself without worrying about
cloning or editing the BitBake layers configuration file
``bblayers.conf``.

Tying a layer source into Toaster is convenient when you have many
custom layers that need to be built on a regular basis by a community of
developers. In fact, Toaster comes pre-configured with the OpenEmbedded
Metadata Index.

.. note::

   You do not have to use a layer source to use Toaster. Tying into a
   layer source is optional.

Setting Up and Using a Layer Source
-----------------------------------

To use your own layer source, you need to set up the layer source and
then tie it into Toaster. This section describes how to tie into a layer
index in a manner similar to the way Toaster ties into the OpenEmbedded
Metadata Index.

Understanding Your Layers
~~~~~~~~~~~~~~~~~~~~~~~~~

The obvious first step for using a layer index is to have several custom
layers that developers build and access using the Yocto Project on a
regular basis. This set of layers needs to exist and you need to be
familiar with where they reside. You will need that information when you
set up the code for the web application that "hooks" into your set of
layers.

For general information on layers, see the
":ref:`overview-manual/yp-intro:the yocto project layer model`"
section in the Yocto Project Overview and Concepts Manual. For information on how
to create layers, see the ":ref:`dev-manual/common-tasks:understanding and creating layers`"
section in the Yocto Project Development Tasks Manual.

Configuring Toaster to Hook Into Your Layer Index
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want Toaster to use your layer index, you must host the web
application in a server to which Toaster can connect. You also need to
give Toaster the information about your layer index. In other words, you
have to configure Toaster to use your layer index. This section
describes two methods by which you can configure and use your layer
index.

In the previous section, the code for the OpenEmbedded Metadata Index
(i.e. :oe_layerindex:`/`) was referenced. You can use
this code, which is at :yocto_git:`/layerindex-web/`, as a base to create
your own layer index.

Use the Administration Interface
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Access the administration interface through a browser by entering the
URL of your Toaster instance and adding "``/admin``" to the end of the
URL. As an example, if you are running Toaster locally, use the
following URL::

   http://127.0.0.1:8000/admin

The administration interface has a "Layer sources" section that includes
an "Add layer source" button. Click that button and provide the required
information. Make sure you select "layerindex" as the layer source type.

Use the Fixture Feature
^^^^^^^^^^^^^^^^^^^^^^^

The Django fixture feature overrides the default layer server when you
use it to specify a custom URL. To use the fixture feature, create (or
edit) the file ``bitbake/lib/toaster.orm/fixtures/custom.xml``, and then
set the following Toaster setting to your custom URL:

.. code-block:: xml

   <?xml version="1.0" ?>
   <django-objects version="1.0">
      <object model="orm.toastersetting" pk="100">
         <field name="name" type="CharField">CUSTOM_LAYERINDEX_SERVER</field>
         <field name="value" type="CharField">https://layers.my_organization.org/layerindex/branch/master/layers/</field>
      </object>
   <django-objects>

When you start Toaster for the first time, or
if you delete the file ``toaster.sqlite`` and restart, the database will
populate cleanly from this layer index server.

Once the information has been updated, verify the new layer information
is available by using the Toaster web interface. To do that, visit the
"All compatible layers" page inside a Toaster project. The layers from
your layer source should be listed there.

If you change the information in your layer index server, refresh the
Toaster database by running the following command:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py lsupdates


If Toaster can reach the API URL, you should see a message telling you that
Toaster is updating the layer source information.

Releases
========

When you create a Toaster project using the web interface, you are asked
to choose a "Release." In the context of Toaster, the term "Release"
refers to a set of layers and a BitBake version the OpenEmbedded build
system uses to build something. As shipped, Toaster is pre-configured
with releases that correspond to Yocto Project release branches.
However, you can modify, delete, and create new releases according to
your needs. This section provides some background information on
releases.

Pre-Configured Releases
-----------------------

As shipped, Toaster is configured to use a specific set of releases. Of
course, you can always configure Toaster to use any release. For
example, you might want your project to build against a specific commit
of any of the "out-of-the-box" releases. Or, you might want your project
to build against different revisions of OpenEmbedded and BitBake.

As shipped, Toaster is configured to work with the following releases:

-  *Yocto Project &DISTRO; "&DISTRO_NAME;" or OpenEmbedded "&DISTRO_NAME;":*
   This release causes your Toaster projects to build against the head
   of the &DISTRO_NAME_NO_CAP; branch at
   :yocto_git:`/poky/log/?h=&DISTRO_NAME_NO_CAP;` or
   :oe_git:`/openembedded-core/commit/?h=&DISTRO_NAME_NO_CAP;`.

-  *Yocto Project "Master" or OpenEmbedded "Master":* This release
   causes your Toaster Projects to build against the head of the master
   branch, which is where active development takes place, at
   :yocto_git:`/poky/log/` or :oe_git:`/openembedded-core/log/`.

-  *Local Yocto Project or Local OpenEmbedded:* This release causes your
   Toaster Projects to build against the head of the ``poky`` or
   ``openembedded-core`` clone you have local to the machine running
   Toaster.

Configuring Toaster
===================

In order to use Toaster, you must configure the database with the
default content. The following subsections describe various aspects of
Toaster configuration.

Configuring the Workflow
------------------------

The ``bldcontrol/management/commands/checksettings.py`` file controls
workflow configuration. The following steps outline the process to
initially populate this database.

1. The default project settings are set from
   ``orm/fixtures/settings.xml``.

2. The default project distro and layers are added from
   ``orm/fixtures/poky.xml`` if poky is installed. If poky is not
   installed, they are added from ``orm/fixtures/oe-core.xml``.

3. If the ``orm/fixtures/custom.xml`` file exists, then its values are
   added.

4. The layer index is then scanned and added to the database.

Once these steps complete, Toaster is set up and ready to use.

Customizing Pre-Set Data
------------------------

The pre-set data for Toaster is easily customizable. You can create the
``orm/fixtures/custom.xml`` file to customize the values that go into to
the database. Customization is additive, and can either extend or
completely replace the existing values.

You use the ``orm/fixtures/custom.xml`` file to change the default
project settings for the machine, distro, file images, and layers. When
creating a new project, you can use the file to define the offered
alternate project release selections. For example, you can add one or
more additional selections that present custom layer sets or distros,
and any other local or proprietary content.

Additionally, you can completely disable the content from the
``oe-core.xml`` and ``poky.xml`` files by defining the section shown
below in the ``settings.xml`` file. For example, this option is
particularly useful if your custom configuration defines fewer releases
or layers than the default fixture files.

The following example sets "name" to "CUSTOM_XML_ONLY" and its value to
"True".

.. code-block:: xml

   <object model="orm.toastersetting" pk="99">
      <field type="CharField" name="name">CUSTOM_XML_ONLY</field>
      <field type="CharField" name="value">True</field>
   </object>

Understanding Fixture File Format
---------------------------------

The following is an overview of the file format used by the
``oe-core.xml``, ``poky.xml``, and ``custom.xml`` files.

The following subsections describe each of the sections in the fixture
files, and outline an example section of the XML code. you can use to
help understand this information and create a local ``custom.xml`` file.

Defining the Default Distro and Other Values
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This section defines the default distro value for new projects. By
default, it reserves the first Toaster Setting record "1". The following
demonstrates how to set the project default value for
:term:`DISTRO`:

.. code-block:: xml

   <!-- Set the project default value for DISTRO -->
   <object model="orm.toastersetting" pk="1">
      <field type="CharField" name="name">DEFCONF_DISTRO</field>
      <field type="CharField" name="value">poky</field>
   </object>

You can override
other default project values by adding additional Toaster Setting
sections such as any of the settings coming from the ``settings.xml``
file. Also, you can add custom values that are included in the BitBake
environment. The "pk" values must be unique. By convention, values that
set default project values have a "DEFCONF" prefix.

Defining BitBake Version
~~~~~~~~~~~~~~~~~~~~~~~~

The following defines which version of BitBake is used for the following
release selection:

.. code-block:: xml

   <!-- Bitbake versions which correspond to the metadata release -->
   <object model="orm.bitbakeversion" pk="1">
      <field type="CharField" name="name">&DISTRO_NAME_NO_CAP;</field>
      <field type="CharField" name="giturl">git://git.yoctoproject.org/poky</field>
      <field type="CharField" name="branch">&DISTRO_NAME_NO_CAP;</field>
      <field type="CharField" name="dirpath">bitbake</field>
   </object>

Defining Release
~~~~~~~~~~~~~~~~

The following defines the releases when you create a new project:

.. code-block:: xml

   <!-- Releases available -->
   <object model="orm.release" pk="1">
      <field type="CharField" name="name">&DISTRO_NAME_NO_CAP;</field>
      <field type="CharField" name="description">Yocto Project &DISTRO; "&DISTRO_NAME;"</field>
      <field rel="ManyToOneRel" to="orm.bitbakeversion" name="bitbake_version">1</field>
      <field type="CharField" name="branch_name">&DISTRO_NAME_NO_CAP;</field>
      <field type="TextField" name="helptext">Toaster will run your builds using the tip of the <a href="http://git.yoctoproject.org/cgit/cgit.cgi/poky/log/?h=&DISTRO_NAME_NO_CAP;">Yocto Project &DISTRO_NAME; branch</a>.</field>
   </object>

The "pk" value must match the above respective BitBake version record.

Defining the Release Default Layer Names
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following defines the default layers for each release:

.. code-block:: xml

   <!-- Default project layers for each release -->
   <object model="orm.releasedefaultlayer" pk="1">
      <field rel="ManyToOneRel" to="orm.release" name="release">1</field>
      <field type="CharField" name="layer_name">openembedded-core</field>
   </object>

The 'pk' values in the example above should start at "1" and increment
uniquely. You can use the same layer name in multiple releases.

Defining Layer Definitions
~~~~~~~~~~~~~~~~~~~~~~~~~~

Layer definitions are the most complex. The following defines each of
the layers, and then defines the exact layer version of the layer used
for each respective release. You must have one ``orm.layer`` entry for
each layer. Then, with each entry you need a set of
``orm.layer_version`` entries that connects the layer with each release
that includes the layer. In general all releases include the layer.

.. code-block:: xml

   <object model="orm.layer" pk="1">
      <field type="CharField" name="name">openembedded-core</field>
      <field type="CharField" name="layer_index_url"></field>
      <field type="CharField" name="vcs_url">git://git.yoctoproject.org/poky</field>
      <field type="CharField" name="vcs_web_url">http://git.yoctoproject.org/cgit/cgit.cgi/poky</field>
      <field type="CharField" name="vcs_web_tree_base_url">http://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/%path%?h=%branch%</field>
      <field type="CharField" name="vcs_web_file_base_url">http://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/%path%?h=%branch%</field>
   </object>
   <object model="orm.layer_version" pk="1">
      <field rel="ManyToOneRel" to="orm.layer" name="layer">1</field>
      <field type="IntegerField" name="layer_source">0</field>
      <field rel="ManyToOneRel" to="orm.release" name="release">1</field>
      <field type="CharField" name="branch">&DISTRO_NAME_NO_CAP;</field>
      <field type="CharField" name="dirpath">meta</field>
   </object> <object model="orm.layer_version" pk="2">
      <field rel="ManyToOneRel" to="orm.layer" name="layer">1</field>
      <field type="IntegerField" name="layer_source">0</field>
      <field rel="ManyToOneRel" to="orm.release" name="release">2</field>
      <field type="CharField" name="branch">HEAD</field>
      <field type="CharField" name="commit">HEAD</field>
      <field type="CharField" name="dirpath">meta</field>
   </object>
   <object model="orm.layer_version" pk="3">
      <field rel="ManyToOneRel" to="orm.layer" name="layer">1</field>
      <field type="IntegerField" name="layer_source">0</field>
      <field rel="ManyToOneRel" to="orm.release" name="release">3</field>
      <field type="CharField" name="branch">master</field>
      <field type="CharField" name="dirpath">meta</field>
   </object>

The layer "pk" values above must be unique, and typically start at "1". The
layer version "pk" values must also be unique across all layers, and typically
start at "1".

Remote Toaster Monitoring
=========================

Toaster has an API that allows remote management applications to
directly query the state of the Toaster server and its builds in a
machine-to-machine manner. This API uses the
`REST <https://en.wikipedia.org/wiki/Representational_state_transfer>`__
interface and the transfer of JSON files. For example, you might monitor
a build inside a container through well supported known HTTP ports in
order to easily access a Toaster server inside the container. In this
example, when you use this direct JSON API, you avoid having web page
parsing against the display the user sees.

Checking Health
---------------

Before you use remote Toaster monitoring, you should do a health check.
To do this, ping the Toaster server using the following call to see if
it is still alive::

   http://host:port/health

Be sure to provide values for host and port. If the server is alive, you will
get the response HTML:

.. code-block:: html

   <!DOCTYPE html>
   <html lang="en">
      <head><title>Toaster Health</title></head>
      <body>Ok</body>
   </html>

Determining Status of Builds in Progress
----------------------------------------

Sometimes it is useful to determine the status of a build in progress.
To get the status of pending builds, use the following call::

   http://host:port/toastergui/api/building

Be sure to provide values for host and port. The output is a JSON file that
itemizes all builds in progress. This file includes the time in seconds since
each respective build started as well as the progress of the cloning, parsing,
and task execution. The following is sample output for a build in progress:

.. code-block:: JSON

   {"count": 1,
    "building": [
      {"machine": "beaglebone",
        "seconds": "463.869",
        "task": "927:2384",
        "distro": "poky",
        "clone": "1:1",
        "id": 2,
        "start": "2017-09-22T09:31:44.887Z",
        "name": "20170922093200",
        "parse": "818:818",
        "project": "my_rocko",
        "target": "core-image-minimal"
      }]
   }

The JSON data for this query is returned in a
single line. In the previous example the line has been artificially
split for readability.

Checking Status of Builds Completed
-----------------------------------

Once a build is completed, you get the status when you use the following
call::

   http://host:port/toastergui/api/builds

Be sure to provide values for host and port. The output is a JSON file that
itemizes all complete builds, and includes build summary information. The
following is sample output for a completed build:

.. code-block:: JSON

   {"count": 1,
    "builds": [
      {"distro": "poky",
         "errors": 0,
         "machine": "beaglebone",
         "project": "my_rocko",
         "stop": "2017-09-22T09:26:36.017Z",
         "target": "quilt-native",
         "seconds": "78.193",
         "outcome": "Succeeded",
         "id": 1,
         "start": "2017-09-22T09:25:17.824Z",
         "warnings": 1,
         "name": "20170922092618"
      }]
   }

The JSON data for this query is returned in a single line. In the
previous example the line has been artificially split for readability.

Determining Status of a Specific Build
--------------------------------------

Sometimes it is useful to determine the status of a specific build. To
get the status of a specific build, use the following call::

   http://host:port/toastergui/api/build/ID

Be sure to provide values for
host, port, and ID. You can find the value for ID from the Builds
Completed query. See the ":ref:`toaster-manual/reference:checking status of builds completed`"
section for more information.

The output is a JSON file that itemizes the specific build and includes
build summary information. The following is sample output for a specific
build:

.. code-block:: JSON

   {"build":
      {"distro": "poky",
       "errors": 0,
       "machine": "beaglebone",
       "project": "my_rocko",
       "stop": "2017-09-22T09:26:36.017Z",
       "target": "quilt-native",
       "seconds": "78.193",
       "outcome": "Succeeded",
       "id": 1,
       "start": "2017-09-22T09:25:17.824Z",
       "warnings": 1,
       "name": "20170922092618",
       "cooker_log": "/opt/user/poky/build-toaster-2/tmp/log/cooker/beaglebone/build_20170922_022607.991.log"
      }
   }

The JSON data for this query is returned in a single line. In the
previous example the line has been artificially split for readability.

Useful Commands
===============

In addition to the web user interface and the scripts that start and
stop Toaster, command-line commands exist through the ``manage.py``
management script. You can find general documentation on ``manage.py``
at the
`Django <https://docs.djangoproject.com/en/2.2/topics/settings/>`__
site. However, several ``manage.py`` commands have been created that are
specific to Toaster and are used to control configuration and back-end
tasks. You can locate these commands in the
:term:`Source Directory` (e.g. ``poky``) at
``bitbake/lib/manage.py``. This section documents those commands.

.. note::

   -  When using ``manage.py`` commands given a default configuration,
      you must be sure that your working directory is set to the
      :term:`Build Directory`. Using
      ``manage.py`` commands from the Build Directory allows Toaster to
      find the ``toaster.sqlite`` file, which is located in the Build
      Directory.

   -  For non-default database configurations, it is possible that you
      can use ``manage.py`` commands from a directory other than the
      Build Directory. To do so, the ``toastermain/settings.py`` file
      must be configured to point to the correct database backend.

``buildslist``
--------------

The ``buildslist`` command lists all builds that Toaster has recorded.
Access the command as follows:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py buildslist

The command returns a list, which includes numeric
identifications, of the builds that Toaster has recorded in the current
database.

You need to run the ``buildslist`` command first to identify existing
builds in the database before using the
:ref:`toaster-manual/reference:\`\`builddelete\`\`` command. Here is an
example that assumes default repository and build directory names:

.. code-block:: shell

   $ cd ~/poky/build
   $ python ../bitbake/lib/toaster/manage.py buildslist

If your Toaster database had only one build, the above
:ref:`toaster-manual/reference:\`\`buildslist\`\``
command would return something like the following::

   1: qemux86 poky core-image-minimal

``builddelete``
---------------

The ``builddelete`` command deletes data associated with a build. Access
the command as follows:

.. code-block::

   $ bitbake/lib/toaster/manage.py builddelete build_id

The command deletes all the build data for the specified
build_id. This command is useful for removing old and unused data from
the database.

Prior to running the ``builddelete`` command, you need to get the ID
associated with builds by using the
:ref:`toaster-manual/reference:\`\`buildslist\`\`` command.

``perf``
--------

The ``perf`` command measures Toaster performance. Access the command as
follows:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py perf

The command is a sanity check that returns page loading times in order to
identify performance problems.

``checksettings``
-----------------

The ``checksettings`` command verifies existing Toaster settings. Access
the command as follows:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py checksettings

Toaster uses settings that are based on the database to configure the
building tasks. The ``checksettings`` command verifies that the database
settings are valid in the sense that they have the minimal information
needed to start a build.

In order for the ``checksettings`` command to work, the database must be
correctly set up and not have existing data. To be sure the database is
ready, you can run the following:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py syncdb
   $ bitbake/lib/toaster/manage.py migrate orm
   $ bitbake/lib/toaster/manage.py migrate bldcontrol

After running these commands, you can run the ``checksettings`` command.

``runbuilds``
-------------

The ``runbuilds`` command launches scheduled builds. Access the command
as follows:

.. code-block:: shell

   $ bitbake/lib/toaster/manage.py runbuilds

The ``runbuilds`` command checks if scheduled builds exist in the database
and then launches them per schedule. The command returns after the builds
start but before they complete. The Toaster Logging Interface records and
updates the database when the builds complete.
