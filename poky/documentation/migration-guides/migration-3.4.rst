.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Migration notes for 3.4 (honister)
----------------------------------

This section provides migration information for moving to the Yocto
Project 3.4 Release (codename "honister") from the prior release.

Override syntax changes
~~~~~~~~~~~~~~~~~~~~~~~

In this release, the ``:`` character replaces the use of ``_`` to
refer to an override, most commonly when making a conditional assignment
of a variable. This means that an entry like::

   SRC_URI_qemux86 = "file://somefile"

now becomes::

   SRC_URI:qemux86 = "file://somefile"

since ``qemux86`` is an override. This applies to any use of override
syntax, so the following::

   SRC_URI_append = " file://somefile"
   SRC_URI_append_qemux86 = " file://somefile2"
   SRC_URI_remove_qemux86-64 = "file://somefile3"
   SRC_URI_prepend_qemuarm = "file://somefile4 "
   FILES_${PN}-ptest = "${bindir}/xyz"
   IMAGE_CMD_tar = "tar"
   BASE_LIB_tune-cortexa76 = "lib"
   SRCREV_pn-bash = "abc"
   BB_TASK_NICE_LEVEL_task-testimage = '0'

would now become::

   SRC_URI:append = " file://somefile"
   SRC_URI:append:qemux86 = " file://somefile2"
   SRC_URI:remove:qemux86-64 = "file://somefile3"
   SRC_URI:prepend:qemuarm = "file://somefile4 "
   FILES:${PN}-ptest = "${bindir}/xyz"
   IMAGE_CMD:tar = "tar"
   BASE_LIB:tune-cortexa76 = "lib"
   SRCREV:pn-bash = "abc"
   BB_TASK_NICE_LEVEL:task-testimage = '0'

This also applies to
:ref:`variable queries to the datastore <bitbake-user-manual/bitbake-user-manual-metadata:functions for accessing datastore variables>`,
for example using ``getVar`` and similar so ``d.getVar("RDEPENDS_${PN}")``
becomes ``d.getVar("RDEPENDS:${PN}")``.

Whilst some of these are fairly obvious such as :term:`MACHINE` and :term:`DISTRO`
overrides, some are less obvious, for example the packaging variables such as
:term:`RDEPENDS`, :term:`FILES` and so on taking package names (e.g. ``${PN}``,
``${PN}-ptest``) as overrides. These overrides are not always in
:term:`OVERRIDES` but applied conditionally in specific contexts
such as packaging. ``task-<taskname>`` is another context specific override, the
context being specific tasks in that case. Tune overrides are another special
case where some code does use them as overrides but some does not. We plan to try
and make the tune code use overrides more consistently in the future.

There are some variables which do not use override syntax which include the
suffix to variables in ``layer.conf`` files such as :term:`BBFILE_PATTERN`,
:term:`SRCREV`\ ``_xxx`` where ``xxx`` is a name from :term:`SRC_URI` and
:term:`PREFERRED_VERSION`\ ``_xxx``. In particular, ``layer.conf`` suffixes
may be the same as a :term:`DISTRO` override causing some confusion. We do
plan to try and improve consistency as these issues are identified.

To help with migration of layers, a script has been provided in OE-Core.
Once configured with the overrides used by a layer, this can be run as::

   <oe-core>/scripts/contrib/convert-overrides.py <layerdir>

.. note::

   Please read the notes in the script as it isn't entirely automatic and it isn't
   expected to handle every case. In particular, it needs to be told which overrides
   the layer uses (usually machine and distro names/overrides) and the result should
   be carefully checked since it can be a little enthusiastic and will convert
   references to ``_append``, ``_remove`` and ``_prepend`` in function and variable
   names.

For reference, this conversion is important as it allows BitBake to more reliably
determine what is an override and what is not, as underscores are also used in
variable names without intending to be overrides. This should allow us to proceed
with other syntax improvements and simplifications for usability. It also means
BitBake no longer has to guess and maintain large lookup lists just in case
e.g. ``functionname`` in ``my_functionname`` is an override, and thus should improve
efficiency.

New host dependencies
~~~~~~~~~~~~~~~~~~~~~

The ``lz4c``, ``pzstd`` and ``zstd`` commands are now required to be
installed on the build host to support LZ4 and Zstandard compression
functionality. These are typically provided by ``lz4`` and ``zstd``
packages in most Linux distributions. Alternatively they are available
as part of :term:`buildtools` tarball if your distribution does not provide
them. For more information see
:ref:`ref-manual/system-requirements:required packages for the build host`.

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

- ``assimp``: problematic from a licensing perspective and no longer
  needed by anything else
- ``clutter-1.0``: legacy component moved to meta-gnome
- ``clutter-gst-3.0``: legacy component moved to meta-gnome
- ``clutter-gtk-1.0``: legacy component moved to meta-gnome
- ``cogl-1.0``: legacy component moved to meta-gnome
- ``core-image-clutter``: removed along with clutter
- ``linux-yocto``: removed version 5.4 recipes (5.14 and 5.10 still
  provided)
- ``mklibs-native``: not actively tested and upstream mklibs still
  requires Python 2
- ``mx-1.0``: obsolete (last release 2012) and isn't used by anything in
  any known layer
- ``packagegroup-core-clutter``: removed along with clutter

Removed classes
~~~~~~~~~~~~~~~

- ``clutter``: moved to meta-gnome along with clutter itself
- ``image-mklibs``: not actively tested and upstream mklibs still
  requires Python 2
- ``meta``: no longer useful. Recipes that need to skip installing
  packages should inherit :ref:`ref-classes-nopackages` instead.

Prelinking disabled by default
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Recent tests have shown that prelinking works only when PIE is not
enabled (see `here <https://rlbl.me/prelink-1>`__ and `here <https://rlbl.me/prelink-2>`__),
and as PIE is both a desirable security feature, and the only
configuration provided and tested by the Yocto Project, there is
simply no sense in continuing to enable prelink.

There's also a concern that no one is maintaining the code, and there
are open bugs (including :yocto_bugs:`this serious one </show_bug.cgi?id=14429>`).
Given that prelink does intricate address arithmetic and rewriting
of binaries the best option is to disable the feature. It is recommended
that you consider disabling this feature in your own configuration if
it is currently enabled.

Virtual runtime provides
~~~~~~~~~~~~~~~~~~~~~~~~

Recipes shouldn't use the ``virtual/`` string in :term:`RPROVIDES` and
:term:`RDEPENDS` --- it is confusing because ``virtual/`` has no special
meaning in :term:`RPROVIDES` and :term:`RDEPENDS` (unlike in the
corresponding build-time :term:`PROVIDES` and :term:`DEPENDS`).

Tune files moved to architecture-specific directories
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The tune files found in ``conf/machine/include`` have now been moved
into their respective architecture name directories under that same
location; e.g. x86 tune files have moved into an ``x86`` subdirectory,
MIPS tune files have moved into a ``mips`` subdirectory, etc.
The ARM tunes have an extra level (``armv8a``, ``armv8m``, etc.) and
some have been renamed to make them uniform with the rest of the tunes.
See :yocto_git:`this commit </poky/commit/?id=1d381f21f5f13aa0c4e1a45683ed656ebeedd37d>`
for reference.

If you have any references to tune files (e.g. in custom machine
configuration files) they will need to be updated.

Extensible SDK host extension
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For a normal SDK, some layers append to :term:`TOOLCHAIN_HOST_TASK`
unconditionally which is fine, until the eSDK tries to override the
variable to its own values. Instead of installing packages specified
in this variable it uses native recipes instead --- a very different
approach. This has led to confusing errors when binaries are added
to the SDK but not relocated.

To avoid these issues, a new :term:`TOOLCHAIN_HOST_TASK_ESDK` variable has
been created. If you wish to extend what is installed in the host
portion of the eSDK then you will now need to set this variable.

Package/recipe splitting
~~~~~~~~~~~~~~~~~~~~~~~~

- ``perl-cross`` has been split out from the main ``perl`` recipe to
  its own ``perlcross`` recipe for maintenance reasons. If you have
  bbappends for the perl recipe then these may need extending.

- The ``wayland`` recipe now packages its binaries in a
  ``wayland-tools`` package rather than putting them into
  ``wayland-dev``.

- Xwayland has been split out of the xserver-xorg tree and thus is now
  in its own ``xwayland`` recipe. If you need Xwayland in your image
  then you may now need to add it explicitly.

- The ``rpm`` package no longer has ``rpm-build`` in its :term:`RRECOMMENDS`;
  if by chance you still need rpm package building functionality in
  your image and you have not already done so then you should add
  ``rpm-build`` to your image explicitly.

- The Python ``statistics`` standard module is now packaged in its own
  ``python3-statistics`` package instead of ``python3-misc`` as
  previously.

Image / SDK generation changes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- Recursive dependencies on the :ref:`ref-tasks-build` task are now disabled when
  building SDKs. These are generally not needed; in the unlikely event
  that you do encounter problems then it will probably be as a result of
  missing explicit dependencies that need to be added.

- Errors during "complementary" package installation (e.g. for ``*-dbg``
  and ``*-dev`` packages) during image construction are no longer
  ignored. Historically some of these packages had installation problems,
  that is no longer the case. In the unlikely event that you see errors
  as a result, you will need to fix the installation/packaging issues.

- When building an image, only packages that will be used in building
  the image (i.e. the first entry in :term:`PACKAGE_CLASSES`) will be
  produced if multiple package types are enabled (which is not a typical
  configuration). If in your CI system you need to have the original
  behaviour, use ``bitbake --runall build <target>``.

- The ``-lic`` package is no longer automatically added to
  :term:`RRECOMMENDS` for every other package when
  :term:`LICENSE_CREATE_PACKAGE` is set to "1". If you wish all license
  packages to be installed corresponding to packages in your image, then
  you should instead add the new ``lic-pkgs`` feature to
  :term:`IMAGE_FEATURES`.

Miscellaneous
~~~~~~~~~~~~~

- Certificates are now properly checked when BitBake fetches sources
  over HTTPS. If you receive errors as a result for your custom recipes,
  you will need to use a mirror or address the issue with the operators
  of the server in question.

- ``avahi`` has had its GTK+ support disabled by default. If you wish to
  re-enable it, set ``AVAHI_GTK = "gtk3"`` in a bbappend for the
  ``avahi`` recipe or in your custom distro configuration file.

- Setting the ``BUILD_REPRODUCIBLE_BINARIES`` variable to "0" no longer
  uses a strangely old fallback date of April 2011, it instead disables
  building reproducible binaries as you would logically expect.

- Setting noexec/nostamp/fakeroot varflags to any value besides "1" will
  now trigger a warning. These should be either set to "1" to enable, or
  not set at all to disable.

- The previously deprecated ``COMPRESS_CMD`` and
  ``CVE_CHECK_CVE_WHITELIST`` variables have been removed. Use
  :term:`CONVERSION_CMD` and ``CVE_CHECK_WHITELIST`` (replaced by
  :term:`CVE_CHECK_IGNORE` in version 4.0) respectively
  instead.

- The obsolete ``oe_machinstall`` function previously provided in the
  :ref:`ref-classes-utils` class has been removed. For
  machine-specific installation it is recommended that you use the
  built-in override support in the fetcher or overrides in general
  instead.

- The ``-P`` (``--clear-password``) option can no longer be used with
  ``useradd`` and ``usermod`` entries in :term:`EXTRA_USERS_PARAMS`.
  It was being implemented using a custom patch to the ``shadow`` recipe
  which clashed with a ``-P`` option that was added upstream in
  ``shadow`` version 4.9, and in any case is fundamentally insecure.
  Hardcoded passwords are still supported but they need to be hashed, see
  examples in :term:`EXTRA_USERS_PARAMS`.


