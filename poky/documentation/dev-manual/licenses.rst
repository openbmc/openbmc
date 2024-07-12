.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Working With Licenses
*********************

As mentioned in the ":ref:`overview-manual/development-environment:licensing`"
section in the Yocto Project Overview and Concepts Manual, open source
projects are open to the public and they consequently have different
licensing structures in place. This section describes the mechanism by
which the :term:`OpenEmbedded Build System`
tracks changes to
licensing text and covers how to maintain open source license compliance
during your project's lifecycle. The section also describes how to
enable commercially licensed recipes, which by default are disabled.

Tracking License Changes
========================

The license of an upstream project might change in the future. In order
to prevent these changes going unnoticed, the
:term:`LIC_FILES_CHKSUM`
variable tracks changes to the license text. The checksums are validated
at the end of the configure step, and if the checksums do not match, the
build will fail.

Specifying the ``LIC_FILES_CHKSUM`` Variable
--------------------------------------------

The :term:`LIC_FILES_CHKSUM` variable contains checksums of the license text
in the source code for the recipe. Here is an example of how to
specify :term:`LIC_FILES_CHKSUM`::

   LIC_FILES_CHKSUM = "file://COPYING;md5=xxxx \
                       file://licfile1.txt;beginline=5;endline=29;md5=yyyy \
                       file://licfile2.txt;endline=50;md5=zzzz \
                       ..."

.. note::

   -  When using "beginline" and "endline", realize that line numbering
      begins with one and not zero. Also, the included lines are
      inclusive (i.e. lines five through and including 29 in the
      previous example for ``licfile1.txt``).

   -  When a license check fails, the selected license text is included
      as part of the QA message. Using this output, you can determine
      the exact start and finish for the needed license text.

The build system uses the :term:`S`
variable as the default directory when searching files listed in
:term:`LIC_FILES_CHKSUM`. The previous example employs the default
directory.

Consider this next example::

   LIC_FILES_CHKSUM = "file://src/ls.c;beginline=5;endline=16;\
                                       md5=bb14ed3c4cda583abc85401304b5cd4e"
   LIC_FILES_CHKSUM = "file://${WORKDIR}/license.html;md5=5c94767cedb5d6987c902ac850ded2c6"

The first line locates a file in ``${S}/src/ls.c`` and isolates lines
five through 16 as license text. The second line refers to a file in
:term:`WORKDIR`.

Note that :term:`LIC_FILES_CHKSUM` variable is mandatory for all recipes,
unless the :term:`LICENSE` variable is set to "CLOSED".

Explanation of Syntax
---------------------

As mentioned in the previous section, the :term:`LIC_FILES_CHKSUM` variable
lists all the important files that contain the license text for the
source code. It is possible to specify a checksum for an entire file, or
a specific section of a file (specified by beginning and ending line
numbers with the "beginline" and "endline" parameters, respectively).
The latter is useful for source files with a license notice header,
README documents, and so forth. If you do not use the "beginline"
parameter, then it is assumed that the text begins on the first line of
the file. Similarly, if you do not use the "endline" parameter, it is
assumed that the license text ends with the last line of the file.

The "md5" parameter stores the md5 checksum of the license text. If the
license text changes in any way as compared to this parameter then a
mismatch occurs. This mismatch triggers a build failure and notifies the
developer. Notification allows the developer to review and address the
license text changes. Also note that if a mismatch occurs during the
build, the correct md5 checksum is placed in the build log and can be
easily copied to the recipe.

There is no limit to how many files you can specify using the
:term:`LIC_FILES_CHKSUM` variable. Generally, however, every project
requires a few specifications for license tracking. Many projects have a
"COPYING" file that stores the license information for all the source
code files. This practice allows you to just track the "COPYING" file as
long as it is kept up to date.

.. note::

   -  If you specify an empty or invalid "md5" parameter,
      :term:`BitBake` returns an md5
      mis-match error and displays the correct "md5" parameter value
      during the build. The correct parameter is also captured in the
      build log.

   -  If the whole file contains only license text, you do not need to
      use the "beginline" and "endline" parameters.

Enabling Commercially Licensed Recipes
======================================

By default, the OpenEmbedded build system disables components that have
commercial or other special licensing requirements. Such requirements
are defined on a recipe-by-recipe basis through the
:term:`LICENSE_FLAGS` variable
definition in the affected recipe. For instance, the
``poky/meta/recipes-multimedia/gstreamer/gst-plugins-ugly`` recipe
contains the following statement::

   LICENSE_FLAGS = "commercial"

Here is a
slightly more complicated example that contains both an explicit recipe
name and version (after variable expansion)::

   LICENSE_FLAGS = "license_${PN}_${PV}"

It is possible to give more details about a specific license
using flags on the :term:`LICENSE_FLAGS_DETAILS` variable::

   LICENSE_FLAGS_DETAILS[my-eula-license] = "For further details, see https://example.com/eula."

If set, this will be displayed to the user if the license hasn't been accepted.

In order for a component restricted by a
:term:`LICENSE_FLAGS` definition to be enabled and included in an image, it
needs to have a matching entry in the global
:term:`LICENSE_FLAGS_ACCEPTED`
variable, which is a variable typically defined in your ``local.conf``
file. For example, to enable the
``poky/meta/recipes-multimedia/gstreamer/gst-plugins-ugly`` package, you
could add either the string "commercial_gst-plugins-ugly" or the more
general string "commercial" to :term:`LICENSE_FLAGS_ACCEPTED`. See the
":ref:`dev-manual/licenses:license flag matching`" section for a full
explanation of how :term:`LICENSE_FLAGS` matching works. Here is the
example::

   LICENSE_FLAGS_ACCEPTED = "commercial_gst-plugins-ugly"

Likewise, to additionally enable the package built from the recipe
containing ``LICENSE_FLAGS = "license_${PN}_${PV}"``, and assuming that
the actual recipe name was ``emgd_1.10.bb``, the following string would
enable that package as well as the original ``gst-plugins-ugly``
package::

   LICENSE_FLAGS_ACCEPTED = "commercial_gst-plugins-ugly license_emgd_1.10"

As a convenience, you do not need to specify the
complete license string for every package. You can use
an abbreviated form, which consists of just the first portion or
portions of the license string before the initial underscore character
or characters. A partial string will match any license that contains the
given string as the first portion of its license. For example, the
following value will also match both of the packages
previously mentioned as well as any other packages that have licenses
starting with "commercial" or "license"::

   LICENSE_FLAGS_ACCEPTED = "commercial license"

License Flag Matching
---------------------

License flag matching allows you to control what recipes the
OpenEmbedded build system includes in the build. Fundamentally, the
build system attempts to match :term:`LICENSE_FLAGS` strings found in
recipes against strings found in :term:`LICENSE_FLAGS_ACCEPTED`.
A match causes the build system to include a recipe in the
build, while failure to find a match causes the build system to exclude
a recipe.

In general, license flag matching is simple. However, understanding some
concepts will help you correctly and effectively use matching.

Before a flag defined by a particular recipe is tested against the
entries of :term:`LICENSE_FLAGS_ACCEPTED`, the expanded
string ``_${PN}`` is appended to the flag. This expansion makes each
:term:`LICENSE_FLAGS` value recipe-specific. After expansion, the
string is then matched against the entries. Thus, specifying
``LICENSE_FLAGS = "commercial"`` in recipe "foo", for example, results
in the string ``"commercial_foo"``. And, to create a match, that string
must appear among the entries of :term:`LICENSE_FLAGS_ACCEPTED`.

Judicious use of the :term:`LICENSE_FLAGS` strings and the contents of the
:term:`LICENSE_FLAGS_ACCEPTED` variable allows you a lot of flexibility for
including or excluding recipes based on licensing. For example, you can
broaden the matching capabilities by using license flags string subsets
in :term:`LICENSE_FLAGS_ACCEPTED`.

.. note::

   When using a string subset, be sure to use the part of the expanded
   string that precedes the appended underscore character (e.g.
   ``usethispart_1.3``, ``usethispart_1.4``, and so forth).

For example, simply specifying the string "commercial" in the
:term:`LICENSE_FLAGS_ACCEPTED` variable matches any expanded
:term:`LICENSE_FLAGS` definition that starts with the string
"commercial" such as "commercial_foo" and "commercial_bar", which
are the strings the build system automatically generates for
hypothetical recipes named "foo" and "bar" assuming those recipes simply
specify the following::

   LICENSE_FLAGS = "commercial"

Thus, you can choose to exhaustively enumerate each license flag in the
list and allow only specific recipes into the image, or you can use a
string subset that causes a broader range of matches to allow a range of
recipes into the image.

This scheme works even if the :term:`LICENSE_FLAGS` string already has
``_${PN}`` appended. For example, the build system turns the license
flag "commercial_1.2_foo" into "commercial_1.2_foo_foo" and would match
both the general "commercial" and the specific "commercial_1.2_foo"
strings found in the :term:`LICENSE_FLAGS_ACCEPTED` variable, as expected.

Here are some other scenarios:

-  You can specify a versioned string in the recipe such as
   "commercial_foo_1.2" in a "foo" recipe. The build system expands this
   string to "commercial_foo_1.2_foo". Combine this license flag with a
   :term:`LICENSE_FLAGS_ACCEPTED` variable that has the string
   "commercial" and you match the flag along with any other flag that
   starts with the string "commercial".

-  Under the same circumstances, you can add "commercial_foo" in the
   :term:`LICENSE_FLAGS_ACCEPTED` variable and the build system not only
   matches "commercial_foo_1.2" but also matches any license flag with
   the string "commercial_foo", regardless of the version.

-  You can be very specific and use both the package and version parts
   in the :term:`LICENSE_FLAGS_ACCEPTED` list (e.g.
   "commercial_foo_1.2") to specifically match a versioned recipe.

Other Variables Related to Commercial Licenses
----------------------------------------------

There are other helpful variables related to commercial license handling,
defined in the
``poky/meta/conf/distro/include/default-distrovars.inc`` file::

   COMMERCIAL_AUDIO_PLUGINS ?= ""
   COMMERCIAL_VIDEO_PLUGINS ?= ""

If you want to enable these components, you can do so by making sure you have
statements similar to the following in your ``local.conf`` configuration file::

   COMMERCIAL_AUDIO_PLUGINS = "gst-plugins-ugly-mad \
       gst-plugins-ugly-mpegaudioparse"
   COMMERCIAL_VIDEO_PLUGINS = "gst-plugins-ugly-mpeg2dec \
       gst-plugins-ugly-mpegstream gst-plugins-bad-mpegvideoparse"
   LICENSE_FLAGS_ACCEPTED = "commercial_gst-plugins-ugly commercial_gst-plugins-bad commercial_qmmp"

Of course, you could also create a matching list for those components using the
more general "commercial" string in the :term:`LICENSE_FLAGS_ACCEPTED` variable,
but that would also enable all the other packages with :term:`LICENSE_FLAGS`
containing "commercial", which you may or may not want::

   LICENSE_FLAGS_ACCEPTED = "commercial"

Specifying audio and video plugins as part of the
:term:`COMMERCIAL_AUDIO_PLUGINS` and :term:`COMMERCIAL_VIDEO_PLUGINS` statements
(along with :term:`LICENSE_FLAGS_ACCEPTED`) includes the plugins or
components into built images, thus adding support for media formats or
components.

.. note::

   GStreamer "ugly" and "bad" plugins are actually available through
   open source licenses. However, the "ugly" ones can be subject to software
   patents in some countries, making it necessary to pay licensing fees
   to distribute them. The "bad" ones are just deemed unreliable by the
   GStreamer community and should therefore be used with care.

Maintaining Open Source License Compliance During Your Product's Lifecycle
==========================================================================

One of the concerns for a development organization using open source
software is how to maintain compliance with various open source
licensing during the lifecycle of the product. While this section does
not provide legal advice or comprehensively cover all scenarios, it does
present methods that you can use to assist you in meeting the compliance
requirements during a software release.

With hundreds of different open source licenses that the Yocto Project
tracks, it is difficult to know the requirements of each and every
license. However, the requirements of the major FLOSS licenses can begin
to be covered by assuming that there are three main areas of concern:

-  Source code must be provided.

-  License text for the software must be provided.

-  Compilation scripts and modifications to the source code must be
   provided.

There are other requirements beyond the scope of these three and the
methods described in this section (e.g. the mechanism through which
source code is distributed).

As different organizations have different ways of releasing software,
there can be multiple ways of meeting license obligations. At
least, we describe here two methods for achieving compliance:

-  The first method is to use OpenEmbedded's ability to provide
   the source code, provide a list of licenses, as well as
   compilation scripts and source code modifications.

   The remainder of this section describes supported methods to meet
   the previously mentioned three requirements.

-  The second method is to generate a *Software Bill of Materials*
   (:term:`SBoM`), as described in the ":doc:`/dev-manual/sbom`" section.
   Not only do you generate :term:`SPDX` output which can be used meet
   license compliance requirements (except for sharing the build system
   and layers sources for the time being), but this output also includes
   component version and patch information which can be used
   for vulnerability assessment.

Whatever method you choose, prior to releasing images, sources,
and the build system, you should audit all artifacts to ensure
completeness.

.. note::

   The Yocto Project generates a license manifest during image creation
   that is located in
   ``${DEPLOY_DIR}/licenses/${SSTATE_PKGARCH}/<image-name>-<machine>.rootfs-<datestamp>/``
   to assist with any audits.

Providing the Source Code
-------------------------

Compliance activities should begin before you generate the final image.
The first thing you should look at is the requirement that tops the list
for most compliance groups --- providing the source. The Yocto Project has
a few ways of meeting this requirement.

One of the easiest ways to meet this requirement is to provide the
entire :term:`DL_DIR` used by the
build. This method, however, has a few issues. The most obvious is the
size of the directory since it includes all sources used in the build
and not just the source used in the released image. It will include
toolchain source, and other artifacts, which you would not generally
release. However, the more serious issue for most companies is
accidental release of proprietary software. The Yocto Project provides
an :ref:`ref-classes-archiver` class to help avoid some of these concerns.

Before you employ :term:`DL_DIR` or the :ref:`ref-classes-archiver` class, you
need to decide how you choose to provide source. The source
:ref:`ref-classes-archiver` class can generate tarballs and SRPMs and can
create them with various levels of compliance in mind.

One way of doing this (but certainly not the only way) is to release
just the source as a tarball. You can do this by adding the following to
the ``local.conf`` file found in the :term:`Build Directory`::

   INHERIT += "archiver"
   ARCHIVER_MODE[src] = "original"

During the creation of your
image, the source from all recipes that deploy packages to the image is
placed within subdirectories of ``DEPLOY_DIR/sources`` based on the
:term:`LICENSE` for each recipe.
Releasing the entire directory enables you to comply with requirements
concerning providing the unmodified source. It is important to note that
the size of the directory can get large.

A way to help mitigate the size issue is to only release tarballs for
licenses that require the release of source. Let us assume you are only
concerned with GPL code as identified by running the following script:

.. code-block:: shell

   # Script to archive a subset of packages matching specific license(s)
   # Source and license files are copied into sub folders of package folder
   # Must be run from build folder
   #!/bin/bash
   src_release_dir="source-release"
   mkdir -p $src_release_dir
   for a in tmp/deploy/sources/*; do
      for d in $a/*; do
         # Get package name from path
         p=`basename $d`
         p=${p%-*}
         p=${p%-*}
         # Only archive GPL packages (update *GPL* regex for your license check)
         numfiles=`ls tmp/deploy/licenses/$p/*GPL* 2> /dev/null | wc -l`
         if [ $numfiles -ge 1 ]; then
            echo Archiving $p
            mkdir -p $src_release_dir/$p/source
            cp $d/* $src_release_dir/$p/source 2> /dev/null
            mkdir -p $src_release_dir/$p/license
            cp tmp/deploy/licenses/$p/* $src_release_dir/$p/license 2> /dev/null
         fi
      done
   done

At this point, you
could create a tarball from the ``gpl_source_release`` directory and
provide that to the end user. This method would be a step toward
achieving compliance with section 3a of GPLv2 and with section 6 of
GPLv3.

Providing License Text
----------------------

One requirement that is often overlooked is inclusion of license text.
This requirement also needs to be dealt with prior to generating the
final image. Some licenses require the license text to accompany the
binary. You can achieve this by adding the following to your
``local.conf`` file::

   COPY_LIC_MANIFEST = "1"
   COPY_LIC_DIRS = "1"
   LICENSE_CREATE_PACKAGE = "1"

Adding these statements to the
configuration file ensures that the licenses collected during package
generation are included on your image.

.. note::

   Setting all three variables to "1" results in the image having two
   copies of the same license file. One copy resides in
   ``/usr/share/common-licenses`` and the other resides in
   ``/usr/share/license``.

   The reason for this behavior is because
   :term:`COPY_LIC_DIRS` and
   :term:`COPY_LIC_MANIFEST`
   add a copy of the license when the image is built but do not offer a
   path for adding licenses for newly installed packages to an image.
   :term:`LICENSE_CREATE_PACKAGE`
   adds a separate package and an upgrade path for adding licenses to an
   image.

As the source :ref:`ref-classes-archiver` class has already archived the
original unmodified source that contains the license files, you would have
already met the requirements for inclusion of the license information
with source as defined by the GPL and other open source licenses.

Providing Compilation Scripts and Source Code Modifications
-----------------------------------------------------------

At this point, we have addressed all we need prior to generating the
image. The next two requirements are addressed during the final
packaging of the release.

By releasing the version of the OpenEmbedded build system and the layers
used during the build, you will be providing both compilation scripts
and the source code modifications in one step.

If the deployment team has a :ref:`overview-manual/concepts:bsp layer`
and a distro layer, and those
those layers are used to patch, compile, package, or modify (in any way)
any open source software included in your released images, you might be
required to release those layers under section 3 of GPLv2 or section 1
of GPLv3. One way of doing that is with a clean checkout of the version
of the Yocto Project and layers used during your build. Here is an
example:

.. code-block:: shell

   # We built using the dunfell branch of the poky repo
   $ git clone -b dunfell git://git.yoctoproject.org/poky
   $ cd poky
   # We built using the release_branch for our layers
   $ git clone -b release_branch git://git.mycompany.com/meta-my-bsp-layer
   $ git clone -b release_branch git://git.mycompany.com/meta-my-software-layer
   # clean up the .git repos
   $ find . -name ".git" -type d -exec rm -rf {} \;

One thing a development organization might want to consider for end-user
convenience is to modify
``meta-poky/conf/templates/default/bblayers.conf.sample`` to ensure that when
the end user utilizes the released build system to build an image, the
development organization's layers are included in the ``bblayers.conf`` file
automatically::

   # POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
   # changes incompatibly
   POKY_BBLAYERS_CONF_VERSION = "2"

   BBPATH = "${TOPDIR}"
   BBFILES ?= ""

   BBLAYERS ?= " \
     ##OEROOT##/meta \
     ##OEROOT##/meta-poky \
     ##OEROOT##/meta-yocto-bsp \
     ##OEROOT##/meta-mylayer \
     "

Creating and
providing an archive of the :term:`Metadata`
layers (recipes, configuration files, and so forth) enables you to meet
your requirements to include the scripts to control compilation as well
as any modifications to the original source.

Compliance Limitations with Executables Built from Static Libraries
-------------------------------------------------------------------

When package A is added to an image via the :term:`RDEPENDS` or :term:`RRECOMMENDS`
mechanisms as well as explicitly included in the image recipe with
:term:`IMAGE_INSTALL`, and depends on a static linked library recipe B
(``DEPENDS += "B"``), package B will neither appear in the generated license
manifest nor in the generated source tarballs.  This occurs as the
:ref:`ref-classes-license` and :ref:`ref-classes-archiver` classes assume that
only packages included via :term:`RDEPENDS` or :term:`RRECOMMENDS`
end up in the image.

As a result, potential obligations regarding license compliance for package B
may not be met.

The Yocto Project doesn't enable static libraries by default, in part because
of this issue. Before a solution to this limitation is found, you need to
keep in mind that if your root filesystem is built from static libraries,
you will need to manually ensure that your deliveries are compliant
with the licenses of these libraries.

Copying Non Standard Licenses
=============================

Some packages, such as the linux-firmware package, have many licenses
that are not in any way common. You can avoid adding a lot of these
types of common license files, which are only applicable to a specific
package, by using the
:term:`NO_GENERIC_LICENSE`
variable. Using this variable also avoids QA errors when you use a
non-common, non-CLOSED license in a recipe.

Here is an example that uses the ``LICENSE.Abilis.txt`` file as
the license from the fetched source::

   NO_GENERIC_LICENSE[Firmware-Abilis] = "LICENSE.Abilis.txt"

