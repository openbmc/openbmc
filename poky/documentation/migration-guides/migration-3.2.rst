.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 3.2 (gatesgarth)
========================

This section provides migration information for moving to the Yocto
Project 3.2 Release (codename "gatesgarth") from the prior release.

.. _migration-3.2-minimum-system-requirements:

Minimum system requirements
---------------------------

``gcc`` version 6.0 is now required at minimum on the build host. For older
host distributions where this is not available, you can use the
:term:`buildtools-extended` tarball (easily installable using
``scripts/install-buildtools``).


.. _migration-3.2-removed-recipes:

Removed recipes
---------------

The following recipes have been removed:

- ``bjam-native``: replaced by ``boost-build-native``
- ``avahi-ui``: folded into the main ``avahi`` recipe --- the GTK UI can be disabled using :term:`PACKAGECONFIG` for ``avahi``.
- ``build-compare``: no longer needed with the removal of the ``packagefeed-stability`` class
- ``dhcp``: obsolete, functionally replaced by ``dhcpcd`` and ``kea``
- ``libmodulemd-v1``: replaced by ``libmodulemd``
- ``packagegroup-core-device-devel``: obsolete


.. _migration-3.2-removed-classes:

Removed classes
---------------

The following classes (.bbclass files) have been removed:

-  ``spdx``: obsolete --- the Yocto Project is a strong supporter of SPDX, but this class was old code using a dated approach and had the potential to be misleading. The ``meta-sdpxscanner`` layer is a much more modern and active approach to handling this and is recommended as a replacement.

- ``packagefeed-stability``: this class had become obsolete with the advent of hash equivalence and reproducible builds.


pseudo path filtering and mismatch behaviour
--------------------------------------------

pseudo now operates on a filtered subset of files. This is a significant change
to the way pseudo operates within OpenEmbedded --- by default, pseudo monitors and
logs (adds to its database) any file created or modified whilst in a ``fakeroot``
environment. However, there are large numbers of files that we simply don't care
about the permissions of whilst in that ``fakeroot`` context, for example ${:term:`S`}, ${:term:`B`}, ${:term:`T`},
${:term:`SSTATE_DIR`}, the central sstate control directories, and others.

As of this release, new functionality in pseudo is enabled to ignore these
directory trees (controlled using a new :term:`PSEUDO_IGNORE_PATHS` variable)
resulting in a cleaner database with less chance of "stray" mismatches if files
are modified outside pseudo context. It also should reduce some overhead from
pseudo as the interprocess round trip to the server is avoided.

There is a possible complication where some existing recipe may break, for
example, a recipe was found to be writing to ``${B}/install`` for
``make install`` in :ref:`ref-tasks-install` and since ``${B}`` is listed as not to be tracked,
there were errors trying to ``chown root`` for files in this location. Another
example was the ``tcl`` recipe where the source directory :term:`S` is set to a
subdirectory of the source tree but files were written out to the directory
structure above that subdirectory. For these types of cases in your own recipes,
extend :term:`PSEUDO_IGNORE_PATHS` to cover additional paths that pseudo should not
be monitoring.

In addition, pseudo's behaviour on mismatches has now been changed --- rather
than doing what turns out to be a rather dangerous "fixup" if it sees a file
with a different path but the same inode as another file it has previously seen,
pseudo will throw an ``abort()`` and direct you to a :yocto_wiki:`wiki page </Pseudo_Abort>`
that explains how to deal with this.


.. _migration-3.2-multilib-mlprefix:

``MLPREFIX`` now required for multilib when runtime dependencies conditionally added
------------------------------------------------------------------------------------

In order to solve some previously intractable problems with runtime
dependencies and multilib, a change was made that now requires the :term:`MLPREFIX`
value to be explicitly prepended to package names being added as
dependencies (e.g. in :term:`RDEPENDS` and :term:`RRECOMMENDS` values)
where the dependency is conditionally added.

If you have anonymous Python or in-line Python conditionally adding
dependencies in your custom recipes, and you intend for those recipes to
work with multilib, then you will need to ensure that ``${MLPREFIX}``
is prefixed on the package names in the dependencies, for example
(from the ``glibc`` recipe)::

    RRECOMMENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'ldconfig', '${MLPREFIX}ldconfig', '', d)}"

This also applies when conditionally adding packages to :term:`PACKAGES` where
those packages have dependencies, for example (from the ``alsa-plugins`` recipe)::

    PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'alsa-plugins-pulseaudio-conf', '', d)}"
    ...
    RDEPENDS_${PN}-pulseaudio-conf += "\
            ${MLPREFIX}libasound-module-conf-pulse \
            ${MLPREFIX}libasound-module-ctl-pulse \
            ${MLPREFIX}libasound-module-pcm-pulse \
    "


.. _migration-3.2-packagegroup-core-device-devel:

packagegroup-core-device-devel no longer included in images built for qemu* machines
------------------------------------------------------------------------------------

``packagegroup-core-device-devel`` was previously added automatically to
images built for ``qemu*`` machines, however the purpose of the group and what
it should contain is no longer clear, and in general, adding userspace
development items to images is best done at the image/class level; thus this
packagegroup was removed.

This packagegroup previously pulled in the following:

- ``distcc-config``
- ``nfs-export-root``
- ``bash``
- ``binutils-symlinks``

If you still need any of these in your image built for a ``qemu*`` machine
then you will add them explicitly to :term:`IMAGE_INSTALL` or another
appropriate place in the dependency chain for your image (if you have not
already done so).


.. _migration-3.2-dhcp:

DHCP server/client replaced
---------------------------

The ``dhcp`` software package has become unmaintained and thus has been
functionally replaced by ``dhcpcd`` (client) and ``kea`` (server). You will
need to replace references to the recipe/package names as appropriate --- most
commonly, at the package level ``dhcp-client`` should be replaced by
``dhcpcd`` and ``dhcp-server`` should be replaced by ``kea``. If you have any
custom configuration files for these they will need to be adapted --- refer to
the upstream documentation for ``dhcpcd`` and ``kea`` for further details.


.. _migration-3.2-packaging-changes:

Packaging changes
-----------------

- ``python3``: the ``urllib`` Python package has now moved into the core package, as it is used more commonly than just netclient (e.g. email, xml, mimetypes, pydoc). In addition, the ``pathlib`` module is now also part of the core package.

- ``iptables``: ``iptables-apply`` and ``ip6tables-apply`` have been split out to their own package to avoid a bash dependency in the main ``iptables`` package


.. _migration-3.2-package-qa-checks:

Package QA check changes
------------------------

Previously, the following package QA checks triggered warnings, however they can
be indicators of genuine underlying problems and are therefore now treated as
errors:

- :ref:`already-stripped <qa-check-already-stripped>`
- :ref:`compile-host-path <qa-check-compile-host-path>`
- :ref:`installed-vs-shipped <qa-check-installed-vs-shipped>`
- :ref:`ldflags <qa-check-ldflags>`
- :ref:`pn-overrides <qa-check-pn-overrides>`
- :ref:`rpaths <qa-check-rpaths>`
- :ref:`staticdev <qa-check-staticdev>`
- :ref:`unknown-configure-option <qa-check-unknown-configure-option>`
- :ref:`useless-rpaths <qa-check-useless-rpaths>`

In addition, the following new checks were added and default to triggering an error:

- :ref:`shebang-size <qa-check-shebang-size>`: Check for shebang (#!) lines
  longer than 128 characters, which can give an error at runtime depending on
  the operating system.

- :ref:`unhandled-features-check <qa-check-unhandled-features-check>`: Check
  if any of the variables supported by the :ref:`ref-classes-features_check`
  class is set while not inheriting the class itself.

- :ref:`missing-update-alternatives <qa-check-missing-update-alternatives>`:
  Check if the recipe sets the :term:`ALTERNATIVE` variable for any of its
  packages, and does not inherit the :ref:`ref-classes-update-alternatives`
  class.

- A trailing slash or duplicated slashes in the value of :term:`S` or :term:`B`
  will now trigger a warning so that they can be removed and path comparisons
  can be more reliable --- remove any instances of these in your recipes if the
  warning is displayed.


.. _migration-3.2-src-uri-file-globbing:

Globbing no longer supported in ``file://`` entries in ``SRC_URI``
------------------------------------------------------------------

Globbing (``*`` and ``?`` wildcards) in ``file://`` URLs within :term:`SRC_URI`
did not properly support file checksums, thus changes to the source files
would not always change the :ref:`ref-tasks-fetch` task checksum, and consequently would
not ensure that the changed files would be incorporated in subsequent builds.

Unfortunately it is not practical to make globbing work generically here, so
the decision was taken to remove support for globs in ``file://`` URLs.
If you have any usage of these in your recipes, then you will now need to
either add each of the files that you expect to match explicitly, or
alternatively if you still need files to be pulled in dynamically, put the
files into a subdirectory and reference that instead.


.. _migration-3.2-deploydir-clean:

deploy class now cleans ``DEPLOYDIR`` before ``do_deploy``
----------------------------------------------------------

:ref:`ref-tasks-deploy` as implemented in the :ref:`ref-classes-deploy` class
now cleans up ${:term:`DEPLOYDIR`} before running, just as
:ref:`ref-tasks-install` cleans up ${:term:`D`} before running. This reduces
the risk of :term:`DEPLOYDIR` being accidentally contaminated by files from
previous runs, possibly even with different config, in case of incremental
builds.

Most recipes and classes that inherit the :ref:`ref-classes-deploy` class or
interact with :ref:`ref-tasks-deploy` are unlikely to be affected by this
unless they add ``prefuncs`` to :ref:`ref-tasks-deploy` *which also* put files
into ``${DEPLOYDIR}`` --- these should be refactored to use
``do_deploy_prepend`` instead.


.. _migration-3.2-nativesdk-sdk-provides-dummy:

Custom SDK / SDK-style recipes need to include ``nativesdk-sdk-provides-dummy``
-------------------------------------------------------------------------------

All :ref:`ref-classes-nativesdk` packages require ``/bin/sh`` due
to their postinstall scriptlets, thus this package has to be dummy-provided
within the SDK and ``nativesdk-sdk-provides-dummy`` now does this. If you have
a custom SDK recipe (or your own SDK-style recipe similar to e.g.
``buildtools-tarball``), you will need to ensure
``nativesdk-sdk-provides-dummy`` or an equivalent is included in
:term:`TOOLCHAIN_HOST_TASK`.


``ld.so.conf`` now moved back to main ``glibc`` package
-------------------------------------------------------

There are cases where one doesn't want ``ldconfig`` on target (e.g. for
read-only root filesystems, it's rather pointless), yet one still
needs ``/etc/ld.so.conf`` to be present at image build time:

When some recipe installs libraries to a non-standard location, and
therefore installs in a file in ``/etc/ld.so.conf.d/foo.conf``, we
need ``/etc/ld.so.conf`` containing::

  include /etc/ld.so.conf.d/*.conf

in order to get those other locations picked up.

Thus ``/etc/ld.so.conf`` is now in the main ``glibc`` package so that
there's always an ``ld.so.conf`` present when the build-time ``ldconfig``
runs towards the end of image construction.

The ``ld.so.conf`` and ``ld.so.conf.d/*.conf`` files do not take up
significant space (at least not compared to the ~700kB ``ldconfig`` binary), and they
might be needed in case ``ldconfig`` is installable, so they are left
in place after the image is built. Technically it would be possible to
remove them if desired, though it would not be trivial if you still
wanted the build-time ldconfig to function (:term:`ROOTFS_POSTPROCESS_COMMAND`
will not work as ``ldconfig`` is run after the functions referred to
by that variable).


.. _migration-3.2-virgl:

Host DRI drivers now used for GL support within ``runqemu``
-----------------------------------------------------------

``runqemu`` now uses the mesa-native libraries everywhere virgl is used
(i.e. when ``gl``, ``gl-es`` or ``egl-headless`` options are specified),
but instructs them to load DRI drivers from the host. Unfortunately this
may not work well with proprietary graphics drivers such as those from
Nvidia; if you are using such drivers then you may need to switch to an
alternative (such as Nouveau in the case of Nvidia hardware) or avoid
using the GL options.


.. _migration-3.2-initramfs-suffix:

Initramfs images now use a blank suffix
---------------------------------------

The reference :term:`Initramfs` images (``core-image-minimal-initramfs``,
``core-image-tiny-initramfs`` and ``core-image-testmaster-initramfs``) now
set an empty string for :term:`IMAGE_NAME_SUFFIX`, which otherwise defaults
to ``".rootfs"``. These images aren't root filesystems and thus the rootfs
label didn't make sense. If you are looking for the output files generated
by these image recipes directly then you will need to adapt to the new
naming without the ``.rootfs`` part.


.. _migration-3.2-image-artifact-names:

Image artifact name variables now centralised in image-artifact-names class
---------------------------------------------------------------------------

The defaults for the following image artifact name variables have been moved
from ``bitbake.conf`` to a new ``image-artifact-names`` class:

- :term:`IMAGE_BASENAME`
- :term:`IMAGE_LINK_NAME`
- :term:`IMAGE_NAME`
- :term:`IMAGE_NAME_SUFFIX`
- :term:`IMAGE_VERSION_SUFFIX`

Image-related classes now inherit this class, and typically these variables
are only referenced within image recipes so those will be unaffected by this
change. However if you have references to these variables in either a recipe
that is not an image or a class that is enabled globally, then those will
now need to be changed to ``inherit image-artifact-names``.


.. _migration-3.2-misc:

Miscellaneous changes
---------------------

- Support for the long-deprecated ``PACKAGE_GROUP`` variable has now been removed --- replace any remaining instances with :term:`FEATURE_PACKAGES`.
- The ``FILESPATHPKG`` variable, having been previously deprecated, has now been removed. Replace any remaining references with appropriate use of :term:`FILESEXTRAPATHS`.
- Erroneous use of ``inherit +=`` (instead of ``INHERIT +=``) in a configuration file now triggers an error instead of silently being ignored.
- ptest support has been removed from the ``kbd`` recipe, as upstream has moved to autotest which is difficult to work with in a cross-compilation environment.
- ``oe.utils.is_machine_specific()`` and ``oe.utils.machine_paths()`` have been removed as their utility was questionable. In the unlikely event that you have references to these in your own code, then the code will need to be reworked.
- The ``i2ctransfer`` module is now disabled by default when building ``busybox`` in order to be consistent with disabling the other i2c tools there. If you do wish the i2ctransfer module to be built in BusyBox then add ``CONFIG_I2CTRANSFER=y`` to your custom BusyBox configuration.
- In the ``Upstream-Status`` header convention for patches, ``Accepted`` has been replaced with ``Backport`` as these almost always mean the same thing i.e. the patch is already upstream and may need to be removed in a future recipe upgrade. If you are adding these headers to your own patches then use ``Backport`` to indicate that the patch has been sent upstream.
- The ``tune-supersparc.inc`` tune file has been removed as it does not appear to be widely used and no longer works.
