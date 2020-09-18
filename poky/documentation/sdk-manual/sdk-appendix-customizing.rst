.. SPDX-License-Identifier: CC-BY-2.0-UK

******************************
Customizing the Extensible SDK
******************************

This appendix describes customizations you can apply to the extensible
SDK.

Configuring the Extensible SDK
==============================

The extensible SDK primarily consists of a pre-configured copy of the
OpenEmbedded build system from which it was produced. Thus, the SDK's
configuration is derived using that build system and the filters shown
in the following list. When these filters are present, the OpenEmbedded
build system applies them against ``local.conf`` and ``auto.conf``:

-  Variables whose values start with "/" are excluded since the
   assumption is that those values are paths that are likely to be
   specific to the :term:`Build Host`.

-  Variables listed in
   :term:`SDK_LOCAL_CONF_BLACKLIST`
   are excluded. These variables are not allowed through from the
   OpenEmbedded build system configuration into the extensible SDK
   configuration. Typically, these variables are specific to the machine
   on which the build system is running and could be problematic as part
   of the extensible SDK configuration.

   For a list of the variables excluded by default, see the
   :term:`SDK_LOCAL_CONF_BLACKLIST`
   in the glossary of the Yocto Project Reference Manual.

-  Variables listed in
   :term:`SDK_LOCAL_CONF_WHITELIST`
   are included. Including a variable in the value of
   ``SDK_LOCAL_CONF_WHITELIST`` overrides either of the previous two
   filters. The default value is blank.

-  Classes inherited globally with
   :term:`INHERIT` that are listed in
   :term:`SDK_INHERIT_BLACKLIST`
   are disabled. Using ``SDK_INHERIT_BLACKLIST`` to disable these
   classes is the typical method to disable classes that are problematic
   or unnecessary in the SDK context. The default value blacklists the
   :ref:`buildhistory <ref-classes-buildhistory>`
   and :ref:`icecc <ref-classes-icecc>` classes.

Additionally, the contents of ``conf/sdk-extra.conf``, when present, are
appended to the end of ``conf/local.conf`` within the produced SDK,
without any filtering. The ``sdk-extra.conf`` file is particularly
useful if you want to set a variable value just for the SDK and not the
OpenEmbedded build system used to create the SDK.

Adjusting the Extensible SDK to Suit Your Build Host's Setup
============================================================

In most cases, the extensible SDK defaults should work with your :term:`Build
Host`'s setup.
However, some cases exist for which you might consider making
adjustments:

-  If your SDK configuration inherits additional classes using the
   :term:`INHERIT` variable and you
   do not need or want those classes enabled in the SDK, you can
   blacklist them by adding them to the
   :term:`SDK_INHERIT_BLACKLIST`
   variable as described in the fourth bullet of the previous section.

   .. note::

      The default value of
      SDK_INHERIT_BLACKLIST
      is set using the "?=" operator. Consequently, you will need to
      either define the entire list by using the "=" operator, or you
      will need to append a value using either "_append" or the "+="
      operator. You can learn more about these operators in the "
      Basic Syntax
      " section of the BitBake User Manual.

   .

-  If you have classes or recipes that add additional tasks to the
   standard build flow (i.e. the tasks execute as the recipe builds as
   opposed to being called explicitly), then you need to do one of the
   following:

   -  After ensuring the tasks are :ref:`shared
      state <overview-manual/overview-manual-concepts:shared state cache>` tasks (i.e. the
      output of the task is saved to and can be restored from the shared
      state cache) or ensuring the tasks are able to be produced quickly
      from a task that is a shared state task, add the task name to the
      value of
      :term:`SDK_RECRDEP_TASKS`.

   -  Disable the tasks if they are added by a class and you do not need
      the functionality the class provides in the extensible SDK. To
      disable the tasks, add the class to the ``SDK_INHERIT_BLACKLIST``
      variable as described in the previous section.

-  Generally, you want to have a shared state mirror set up so users of
   the SDK can add additional items to the SDK after installation
   without needing to build the items from source. See the "`Providing
   Additional Installable Extensible SDK
   Content <#sdk-providing-additional-installable-extensible-sdk-content>`__"
   section for information.

-  If you want users of the SDK to be able to easily update the SDK, you
   need to set the
   :term:`SDK_UPDATE_URL`
   variable. For more information, see the "`Providing Updates to the
   Extensible SDK After
   Installation <#sdk-providing-updates-to-the-extensible-sdk-after-installation>`__"
   section.

-  If you have adjusted the list of files and directories that appear in
   :term:`COREBASE` (other than
   layers that are enabled through ``bblayers.conf``), then you must
   list these files in
   :term:`COREBASE_FILES` so
   that the files are copied into the SDK.

-  If your OpenEmbedded build system setup uses a different environment
   setup script other than
   :ref:`structure-core-script`, then you must
   set
   :term:`OE_INIT_ENV_SCRIPT`
   to point to the environment setup script you use.

   .. note::

      You must also reflect this change in the value used for the
      COREBASE_FILES
      variable as previously described.

Changing the Extensible SDK Installer Title
===========================================

You can change the displayed title for the SDK installer by setting the
:term:`SDK_TITLE` variable and then
rebuilding the the SDK installer. For information on how to build an SDK
installer, see the "`Building an SDK
Installer <#sdk-building-an-sdk-installer>`__" section.

By default, this title is derived from
:term:`DISTRO_NAME` when it is
set. If the ``DISTRO_NAME`` variable is not set, the title is derived
from the :term:`DISTRO` variable.

The
:ref:`populate_sdk_base <ref-classes-populate-sdk-*>`
class defines the default value of the ``SDK_TITLE`` variable as
follows:
::

   SDK_TITLE ??= "${@d.getVar('DISTRO_NAME') or d.getVar('DISTRO')} SDK"

While several ways exist to change this variable, an efficient method is
to set the variable in your distribution's configuration file. Doing so
creates an SDK installer title that applies across your distribution. As
an example, assume you have your own layer for your distribution named
"meta-mydistro" and you are using the same type of file hierarchy as
does the default "poky" distribution. If so, you could update the
``SDK_TITLE`` variable in the
``~/meta-mydistro/conf/distro/mydistro.conf`` file using the following
form:
::

   SDK_TITLE = "your_title"

Providing Updates to the Extensible SDK After Installation
==========================================================

When you make changes to your configuration or to the metadata and if
you want those changes to be reflected in installed SDKs, you need to
perform additional steps. These steps make it possible for anyone using
the installed SDKs to update the installed SDKs by using the
``devtool sdk-update`` command:

1. Create a directory that can be shared over HTTP or HTTPS. You can do
   this by setting up a web server such as an `Apache HTTP
   Server <https://en.wikipedia.org/wiki/Apache_HTTP_Server>`__ or
   `Nginx <https://en.wikipedia.org/wiki/Nginx>`__ server in the cloud
   to host the directory. This directory must contain the published SDK.

2. Set the
   :term:`SDK_UPDATE_URL`
   variable to point to the corresponding HTTP or HTTPS URL. Setting
   this variable causes any SDK built to default to that URL and thus,
   the user does not have to pass the URL to the ``devtool sdk-update``
   command as described in the "`Applying Updates to an Installed
   Extensible
   SDK <#sdk-applying-updates-to-an-installed-extensible-sdk>`__"
   section.

3. Build the extensible SDK normally (i.e., use the
   ``bitbake -c populate_sdk_ext`` imagename command).

4. Publish the SDK using the following command:
   ::

      $ oe-publish-sdk some_path/sdk-installer.sh path_to_shared_http_directory

   You must
   repeat this step each time you rebuild the SDK with changes that you
   want to make available through the update mechanism.

Completing the above steps allows users of the existing installed SDKs
to simply run ``devtool sdk-update`` to retrieve and apply the latest
updates. See the "`Applying Updates to an Installed Extensible
SDK <#sdk-applying-updates-to-an-installed-extensible-sdk>`__" section
for further information.

Changing the Default SDK Installation Directory
===============================================

When you build the installer for the Extensible SDK, the default
installation directory for the SDK is based on the
:term:`DISTRO` and
:term:`SDKEXTPATH` variables from
within the
:ref:`populate_sdk_base <ref-classes-populate-sdk-*>`
class as follows:
::

   SDKEXTPATH ??= "~/${@d.getVar('DISTRO')}_sdk"

You can
change this default installation directory by specifically setting the
``SDKEXTPATH`` variable.

While a number of ways exist through which you can set this variable,
the method that makes the most sense is to set the variable in your
distribution's configuration file. Doing so creates an SDK installer
default directory that applies across your distribution. As an example,
assume you have your own layer for your distribution named
"meta-mydistro" and you are using the same type of file hierarchy as
does the default "poky" distribution. If so, you could update the
``SDKEXTPATH`` variable in the
``~/meta-mydistro/conf/distro/mydistro.conf`` file using the following
form:
::

   SDKEXTPATH = "some_path_for_your_installed_sdk"

After building your installer, running it prompts the user for
acceptance of the some_path_for_your_installed_sdk directory as the
default location to install the Extensible SDK.

Providing Additional Installable Extensible SDK Content
=======================================================

If you want the users of an extensible SDK you build to be able to add
items to the SDK without requiring the users to build the items from
source, you need to do a number of things:

1. Ensure the additional items you want the user to be able to install
   are already built:

   -  Build the items explicitly. You could use one or more "meta"
      recipes that depend on lists of other recipes.

   -  Build the "world" target and set
      ``EXCLUDE_FROM_WORLD_pn-``\ recipename for the recipes you do not
      want built. See the
      :term:`EXCLUDE_FROM_WORLD`
      variable for additional information.

2. Expose the ``sstate-cache`` directory produced by the build.
   Typically, you expose this directory by making it available through
   an `Apache HTTP
   Server <https://en.wikipedia.org/wiki/Apache_HTTP_Server>`__ or
   `Nginx <https://en.wikipedia.org/wiki/Nginx>`__ server.

3. Set the appropriate configuration so that the produced SDK knows how
   to find the configuration. The variable you need to set is
   :term:`SSTATE_MIRRORS`:
   ::

      SSTATE_MIRRORS = "file://.* http://example.com/some_path/sstate-cache/PATH"

   You can set the
   ``SSTATE_MIRRORS`` variable in two different places:

   -  If the mirror value you are setting is appropriate to be set for
      both the OpenEmbedded build system that is actually building the
      SDK and the SDK itself (i.e. the mirror is accessible in both
      places or it will fail quickly on the OpenEmbedded build system
      side, and its contents will not interfere with the build), then
      you can set the variable in your ``local.conf`` or custom distro
      configuration file. You can then "whitelist" the variable through
      to the SDK by adding the following:
      ::

         SDK_LOCAL_CONF_WHITELIST = "SSTATE_MIRRORS"

   -  Alternatively, if you just want to set the ``SSTATE_MIRRORS``
      variable's value for the SDK alone, create a
      ``conf/sdk-extra.conf`` file either in your
      :term:`Build Directory` or within any
      layer and put your ``SSTATE_MIRRORS`` setting within that file.

      .. note::

         This second option is the safest option should you have any
         doubts as to which method to use when setting
         SSTATE_MIRRORS
         .

Minimizing the Size of the Extensible SDK Installer Download
============================================================

By default, the extensible SDK bundles the shared state artifacts for
everything needed to reconstruct the image for which the SDK was built.
This bundling can lead to an SDK installer file that is a Gigabyte or
more in size. If the size of this file causes a problem, you can build
an SDK that has just enough in it to install and provide access to the
``devtool command`` by setting the following in your configuration:
::

   SDK_EXT_TYPE = "minimal"

Setting
:term:`SDK_EXT_TYPE` to
"minimal" produces an SDK installer that is around 35 Mbytes in size,
which downloads and installs quickly. You need to realize, though, that
the minimal installer does not install any libraries or tools out of the
box. These libraries and tools must be installed either "on the fly" or
through actions you perform using ``devtool`` or explicitly with the
``devtool sdk-install`` command.

In most cases, when building a minimal SDK you need to also enable
bringing in the information on a wider range of packages produced by the
system. Requiring this wider range of information is particularly true
so that ``devtool add`` is able to effectively map dependencies it
discovers in a source tree to the appropriate recipes. Additionally, the
information enables the ``devtool search`` command to return useful
results.

To facilitate this wider range of information, you would need to set the
following:
::

   SDK_INCLUDE_PKGDATA = "1"

See the :term:`SDK_INCLUDE_PKGDATA` variable for additional information.

Setting the ``SDK_INCLUDE_PKGDATA`` variable as shown causes the "world"
target to be built so that information for all of the recipes included
within it are available. Having these recipes available increases build
time significantly and increases the size of the SDK installer by 30-80
Mbytes depending on how many recipes are included in your configuration.

You can use ``EXCLUDE_FROM_WORLD_pn-``\ recipename for recipes you want
to exclude. However, it is assumed that you would need to be building
the "world" target if you want to provide additional items to the SDK.
Consequently, building for "world" should not represent undue overhead
in most cases.

.. note::

   If you set
   SDK_EXT_TYPE
   to "minimal", then providing a shared state mirror is mandatory so
   that items can be installed as needed. See the "
   Providing Additional Installable Extensible SDK Content
   " section for more information.

You can explicitly control whether or not to include the toolchain when
you build an SDK by setting the
:term:`SDK_INCLUDE_TOOLCHAIN`
variable to "1". In particular, it is useful to include the toolchain
when you have set ``SDK_EXT_TYPE`` to "minimal", which by default,
excludes the toolchain. Also, it is helpful if you are building a small
SDK for use with an IDE or some other tool where you do not want to take
extra steps to install a toolchain.
