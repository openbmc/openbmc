.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 4.1 (langdale)
======================

Migration notes for 4.1 (langdale)
-----------------------------------

This section provides migration information for moving to the Yocto
Project 4.1 Release (codename "langdale") from the prior release.


.. _migration-4.1-make-4.0:

make 4.0 is now the minimum required make version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

glibc now requires ``make`` 4.0 to build, thus it is now the version required to
be installed on the build host. A new :term:`buildtools-make` tarball has been
introduced to provide just make 4.0 for host distros without a current/working
make 4.x version; if you also need other tools you can use the updated
:term:`buildtools` tarball. For more information see
:ref:`ref-manual/system-requirements:required packages for the build host`.


.. _migration-4.1-complementary-deps:

Complementary package installation ignores recommends
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When installing complementary packages (e.g. ``-dev`` and ``-dbg`` packages when
building an SDK, or if you have added ``dev-deps`` to :term:`IMAGE_FEATURES`),
recommends (as defined by :term:`RRECOMMENDS`) are no longer installed.

If you wish to double-check the contents of your images after this change, see
:ref:`Checking Image / SDK Changes <migration-general-buildhistory>`. If needed
you can explicitly install items by adding them to :term:`IMAGE_INSTALL` in
image recipes or :term:`TOOLCHAIN_TARGET_TASK` for the SDK.


.. _migration-4.1-dev-recommends:

dev dependencies are now recommends
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The default for ``${PN}-dev`` package is now to use :term:`RRECOMMENDS` instead
of :term:`RDEPENDS` to pull in the main package. This takes advantage of a
change to complimentary package installation to not follow :term:`RRECOMMENDS`
(as mentioned above) and for example means an SDK for an image with both openssh
and dropbear components will now build successfully.


.. _migration-4.1-dropbear-sftp:

dropbear now recommends openssh-sftp-server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

openssh has switched the scp client to use the sftp protocol instead of scp to
move files. This means scp from Fedora 36 and other current distributions will
no longer be able to move files to/from a system running dropbear with no sftp
server installed.

The sftp server from openssh is small (200kb uncompressed) and standalone, so
adding it to the packagegroup seems to be the best way to preserve the
functionality for user sanity. However, if you wish to avoid this dependency,
you can either:

 A. Use ``dropbear`` in :term:`IMAGE_INSTALL` instead of
    ``packagegroup-core-ssh-dropbear`` (or ``ssh-server-dropbear`` in
    :term:`IMAGE_FEATURES`), or
 B. Add ``openssh-sftp-server`` to :term:`BAD_RECOMMENDATIONS`.


.. _migration-4.1-classes-split:

Classes now split by usage context
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A split directory structure has now been set up for ``.bbclass`` files - classes
that are intended to be inherited only by recipes (e.g. ``inherit`` in a recipe
file, :term:`IMAGE_CLASSES` or :term:`KERNEL_CLASSES`) should be in a
``classes-recipe`` subdirectory and classes that are intended to be inherited
globally (e.g. via ``INHERIT +=``, :term:`PACKAGE_CLASSES`, :term:`USER_CLASSES`
or :term:`INHERIT_DISTRO`) should be in ``classes-global``. Classes in the
existing ``classes`` subdirectory will continue to work in any context as before.

Other than knowing where to look when manually browsing the class files, this is
not likely to require any changes to your configuration. However, if in your
configuration you were using some classes in the incorrect context, you will now
receive an error during parsing. For example, the following in ``local.conf`` will
now cause an error::

   INHERIT += "testimage"

Since :ref:`ref-classes-testimage` is a class intended solely to
affect image recipes, this would be correctly specified as::

   IMAGE_CLASSES += "testimage"


.. _migration-4.1-local-file-error:

Missing local files in SRC_URI now triggers an error
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If a file referenced in :term:`SRC_URI` does not exist, in 4.1 this will trigger
an error at parse time where previously this only triggered a warning. In the past
you could ignore these warnings for example if you have multiple build
configurations (e.g. for several different target machines) and there were recipes
that you were not building in one of the configurations. If you have this scenario
you will now need to conditionally add entries to :term:`SRC_URI` where they are
valid, or use :term:`COMPATIBLE_MACHINE` / :term:`COMPATIBLE_HOST` to prevent the
recipe from being available (and therefore avoid it being parsed) in configurations
where the files aren't available.


.. _migration-4.1-qa-checks:

QA check changes
~~~~~~~~~~~~~~~~

- The :ref:`buildpaths <qa-check-buildpaths>` QA check is now enabled by default
  in :term:`WARN_QA`, and thus any build system paths found in output files will
  trigger a warning. If you see these warnings for your own recipes, for full
  binary reproducibility you should make the necessary changes to the recipe build
  to remove these paths. If you wish to disable the warning for a particular
  recipe you can use :term:`INSANE_SKIP`, or for the entire build you can adjust
  :term:`WARN_QA`. For more information, see the :ref:`buildpaths QA check
  <qa-check-buildpaths>` section.

- ``do_qa_staging`` now checks shebang length in all directories specified by
  :term:`SYSROOT_DIRS`, since there is a maximum length defined in the kernel. For
  native recipes which write scripts to the sysroot, if the shebang line in one of
  these scripts is too long you will get an error. This can be skipped using
  :term:`INSANE_SKIP` if necessary, but the best course of action is of course to
  fix the script. There is now also a ``create_cmdline_shebang_wrapper`` function
  that you can call e.g. from ``do_install`` (or ``do_install:append``) within a
  recipe to create a wrapper to fix such scripts - see the ``libcheck`` recipe
  for an example usage.



Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

- ``mount.blacklist`` has been renamed to ``mount.ignorelist`` in
  ``udev-extraconf``. If you are customising this file via ``udev-extraconf`` then
  you will need to update your ``udev-extraconf`` ``.bbappend`` as appropriate.
- ``help2man-native`` has been removed from implicit sysroot dependencies. If a
  recipe needs ``help2man-native`` it should now be explicitly added to
  :term:`DEPENDS` within the recipe.
- For images using systemd, the reboot watchdog timeout has been set to 60
  seconds (from the upstream default of 10 minutes). If you wish to override this
  you can set :term:`WATCHDOG_TIMEOUT` to the desired timeout in seconds. Note
  that the same :term:`WATCHDOG_TIMEOUT` variable also specifies the timeout used
  for the ``watchdog`` tool (if that is being built).
- The :ref:`ref-classes-image-buildinfo` class now writes to
  ``${sysconfdir}/buildinfo`` instead of ``${sysconfdir}/build`` by default (i.e.
  the default value of :term:`IMAGE_BUILDINFO_FILE` has been changed). If you have
  code that reads this from images at build or runtime you will need to update it
  or specify your own value for :term:`IMAGE_BUILDINFO_FILE`.
- In the :ref:`ref-classes-archiver` class, the default
  ``ARCHIVER_OUTDIR`` value no longer includes the :term:`MACHINE` value in order
  to avoid the archive task running multiple times in a multiconfig setup. If you
  have custom code that does something with the files archived by the
  :ref:`ref-classes-archiver` class then you may need to adjust it to
  the new structure.
- If you are not using `systemd` then udev is now configured to use labels
  (``LABEL`` or ``PARTLABEL``) to set the mount point for the device. For example::

    /run/media/rootfs-sda2

  instead of::

    /run/media/sda2

- ``icu`` no longer provides the ``icu-config`` configuration tool - upstream
  have indicated ``icu-config`` is deprecated and should no longer be used. Code
  with references to it will need to be updated, for example to use ``pkg-config``
  instead.
- The ``rng-tools`` systemd service name has changed from ``rngd`` to ``rng-tools``
- The ``largefile`` :term:`DISTRO_FEATURES` item has been removed, large file
  support is now always enabled where it was previously optional.
- The Python ``zoneinfo`` module is now split out to its own ``python3-zoneinfo``
  package.
- The :term:`PACKAGECONFIG` option to enable wpa_supplicant in the ``connman``
  recipe has been renamed to "wpa-supplicant". If you have set :term:`PACKAGECONFIG` for
  the ``connman`` recipe to include this option you will need to update
  your configuration. Related to this, the :term:`WIRELESS_DAEMON` variable
  now expects the new ``wpa-supplicant`` naming and affects ``packagegroup-base``
  as well as ``connman``.
- The ``wpa-supplicant`` recipe no longer uses a static (and stale) ``defconfig``
  file, instead it uses the upstream version with appropriate edits for the
  :term:`PACKAGECONFIG`. If you are customising this file you will need to
  update your customisations.
- With the introduction of picobuild in
  :ref:`ref-classes-python_pep517`, The ``PEP517_BUILD_API``
  variable is no longer supported. If you have any references to this variable
  you should remove them.


.. _migration-4.1-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

- ``alsa-utils-scripts``: merged into alsa-utils
- ``cargo-cross-canadian``: optimised out
- ``lzop``: obsolete, unmaintained upstream
- ``linux-yocto (5.10)``: 5.15 and 5.19 are currently provided
- ``rust-cross``: optimised out
- ``rust-crosssdk``: optimised out
- ``rust-tools-cross-canadian``: optimised out
- ``xf86-input-keyboard``: obsolete (replaced by libinput/evdev)
