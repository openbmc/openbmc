.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using an External SCM
*********************

If you're working on a recipe that pulls from an external Source Code
Manager (SCM), it is possible to have the OpenEmbedded build system
notice new recipe changes added to the SCM and then build the resulting
packages that depend on the new recipes by using the latest versions.
This only works for SCMs from which it is possible to get a sensible
revision number for changes. Currently, you can do this with Apache
Subversion (SVN), Git, and Bazaar (BZR) repositories.

To enable this behavior, the :term:`PV` of
the recipe needs to reference
:term:`SRCPV`. Here is an example::

   PV = "1.2.3+git${SRCPV}"

Then, you can add the following to your
``local.conf``::

   SRCREV:pn-PN = "${AUTOREV}"

:term:`PN` is the name of the recipe for
which you want to enable automatic source revision updating.

If you do not want to update your local configuration file, you can add
the following directly to the recipe to finish enabling the feature::

   SRCREV = "${AUTOREV}"

The Yocto Project provides a distribution named ``poky-bleeding``, whose
configuration file contains the line::

   require conf/distro/include/poky-floating-revisions.inc

This line pulls in the
listed include file that contains numerous lines of exactly that form::

   #SRCREV:pn-opkg-native ?= "${AUTOREV}"
   #SRCREV:pn-opkg-sdk ?= "${AUTOREV}"
   #SRCREV:pn-opkg ?= "${AUTOREV}"
   #SRCREV:pn-opkg-utils-native ?= "${AUTOREV}"
   #SRCREV:pn-opkg-utils ?= "${AUTOREV}"
   SRCREV:pn-gconf-dbus ?= "${AUTOREV}"
   SRCREV:pn-matchbox-common ?= "${AUTOREV}"
   SRCREV:pn-matchbox-config-gtk ?= "${AUTOREV}"
   SRCREV:pn-matchbox-desktop ?= "${AUTOREV}"
   SRCREV:pn-matchbox-keyboard ?= "${AUTOREV}"
   SRCREV:pn-matchbox-panel-2 ?= "${AUTOREV}"
   SRCREV:pn-matchbox-themes-extra ?= "${AUTOREV}"
   SRCREV:pn-matchbox-terminal ?= "${AUTOREV}"
   SRCREV:pn-matchbox-wm ?= "${AUTOREV}"
   SRCREV:pn-settings-daemon ?= "${AUTOREV}"
   SRCREV:pn-screenshot ?= "${AUTOREV}"
   . . .

These lines allow you to
experiment with building a distribution that tracks the latest
development source for numerous packages.

.. note::

   The ``poky-bleeding`` distribution is not tested on a regular basis. Keep
   this in mind if you use it.

