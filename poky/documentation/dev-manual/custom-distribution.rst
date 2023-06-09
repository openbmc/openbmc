.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Creating Your Own Distribution
******************************

When you build an image using the Yocto Project and do not alter any
distribution :term:`Metadata`, you are
creating a Poky distribution. If you wish to gain more control over
package alternative selections, compile-time options, and other
low-level configurations, you can create your own distribution.

To create your own distribution, the basic steps consist of creating
your own distribution layer, creating your own distribution
configuration file, and then adding any needed code and Metadata to the
layer. The following steps provide some more detail:

-  *Create a layer for your new distro:* Create your distribution layer
   so that you can keep your Metadata and code for the distribution
   separate. It is strongly recommended that you create and use your own
   layer for configuration and code. Using your own layer as compared to
   just placing configurations in a ``local.conf`` configuration file
   makes it easier to reproduce the same build configuration when using
   multiple build machines. See the
   ":ref:`dev-manual/layers:creating a general layer using the \`\`bitbake-layers\`\` script`"
   section for information on how to quickly set up a layer.

-  *Create the distribution configuration file:* The distribution
   configuration file needs to be created in the ``conf/distro``
   directory of your layer. You need to name it using your distribution
   name (e.g. ``mydistro.conf``).

   .. note::

      The :term:`DISTRO` variable in your ``local.conf`` file determines the
      name of your distribution.

   You can split out parts of your configuration file into include files
   and then "require" them from within your distribution configuration
   file. Be sure to place the include files in the
   ``conf/distro/include`` directory of your layer. A common example
   usage of include files would be to separate out the selection of
   desired version and revisions for individual recipes.

   Your configuration file needs to set the following required
   variables:

   - :term:`DISTRO_NAME`

   - :term:`DISTRO_VERSION`

   These following variables are optional and you typically set them
   from the distribution configuration file:

   - :term:`DISTRO_FEATURES`

   - :term:`DISTRO_EXTRA_RDEPENDS`

   - :term:`DISTRO_EXTRA_RRECOMMENDS`

   - :term:`TCLIBC`

   .. tip::

      If you want to base your distribution configuration file on the
      very basic configuration from OE-Core, you can use
      ``conf/distro/defaultsetup.conf`` as a reference and just include
      variables that differ as compared to ``defaultsetup.conf``.
      Alternatively, you can create a distribution configuration file
      from scratch using the ``defaultsetup.conf`` file or configuration files
      from another distribution such as Poky as a reference.

-  *Provide miscellaneous variables:* Be sure to define any other
   variables for which you want to create a default or enforce as part
   of the distribution configuration. You can include nearly any
   variable from the ``local.conf`` file. The variables you use are not
   limited to the list in the previous bulleted item.

-  *Point to Your distribution configuration file:* In your ``local.conf``
   file in the :term:`Build Directory`, set your :term:`DISTRO` variable to
   point to your distribution's configuration file. For example, if your
   distribution's configuration file is named ``mydistro.conf``, then
   you point to it as follows::

      DISTRO = "mydistro"

-  *Add more to the layer if necessary:* Use your layer to hold other
   information needed for the distribution:

   -  Add recipes for installing distro-specific configuration files
      that are not already installed by another recipe. If you have
      distro-specific configuration files that are included by an
      existing recipe, you should add an append file (``.bbappend``) for
      those. For general information and recommendations on how to add
      recipes to your layer, see the
      ":ref:`dev-manual/layers:creating your own layer`" and
      ":ref:`dev-manual/layers:following best practices when creating layers`"
      sections.

   -  Add any image recipes that are specific to your distribution.

   -  Add a ``psplash`` append file for a branded splash screen, using
      the :term:`SPLASH_IMAGES` variable.

   -  Add any other append files to make custom changes that are
      specific to individual recipes.

   For information on append files, see the
   ":ref:`dev-manual/layers:appending other layers metadata with your layer`"
   section.
