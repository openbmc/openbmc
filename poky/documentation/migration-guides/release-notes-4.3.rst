.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 4.3 (nanbield)
--------------------------------

New Features / Enhancements in 4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.5 and 6.1, gcc 13, glibc 2.38, LLVM 17, and over 300 other recipe upgrades

-  The autobuilder's shared-state artefacts are now available over the `jsDelivr
   <https://jsdelivr.com>`__ Content Delivery Network (CDN).
   See :term:`SSTATE_MIRRORS`.

-  New variables:

   -  :term:`CVE_CHECK_STATUSMAP`, :term:`CVE_STATUS`, :term:`CVE_STATUS_GROUPS`,
      replacing the deprecated :term:`CVE_CHECK_IGNORE`.

   -  :term:`FILE_LAYERNAME`: bitbake now sets this to the name of the layer
      containing the recipe

   -  :term:`FIT_ADDRESS_CELLS` and :term:`UBOOT_FIT_ADDRESS_CELLS`.
      See details below.

   -  :term:`KERNEL_DTBDEST`: directory where to install DTB files.

   -  :term:`KERNEL_DTBVENDORED`: whether to keep vendor subdirectories.

   -  :term:`KERNEL_LOCALVERSION`: to add a string to the kernel version
      information.

   -  :term:`KERNEL_STRIP`: to specify the command to strip the kernel binary.

   -  :term:`LICENSE_FLAGS_DETAILS`: add extra details about a recipe license
      in case it is not allowed by :term:`LICENSE_FLAGS_ACCEPTED`.

   -  :term:`MESON_TARGET`: to compile a specific Meson target instead of the
      default ones.

   -  :term:`OEQA_REPRODUCIBLE_TEST_PACKAGE`: to restrict package managers used
      in reproducibility testing.

-  Layername functionality available through overrides

   Code can now know which layer a recipe is coming from through the newly added :term:`FILE_LAYERNAME`
   variable. This has been added as an override of the form ``layer-<layername>``. In particular,
   this means QA checks can now be layer specific, for example::

      ERROR_QA:layer-core:append = " patch-status"

   This will enable the ``patch-status`` QA check for the core layer.

-  Architecture-specific enhancements:

   -  RISCV support is now enabled in LLVM 17.

   -  Loongarch support in the :ref:`ref-classes-linuxloader` class and
      ``core-image-minimal-initramfs`` image.

   -  The ``arch-armv8`` and ``arch-armv9`` architectures are now given
      `Scalable Vector Extension (SVE)
      <https://developer.arm.com/documentation/100891/0612/sve-overview/introducing-sve>`__
      based tune options. Commits:
      :yocto_git:`1 </poky/commit/?id=e4be03be5be62e367a40437a389121ef97d6cff3>`,
      :yocto_git:`2 </poky/commit/?id=8cd5d264af4c346730531cb98ae945ab862dbd69>`.

   -  Many changes to support 64-bit ``time_t`` on 32-bit architectures

-  Kernel-related enhancements:

   - The default kernel is the current stable (6.5), and there is also support
     for the latest long-term release (6.1).

   - The list of fixed kernel CVEs is updated regularly using data from
     `linuxkernelcves.com <https://linuxkernelcves.com>`__.

   - A ``showconfig`` task was added to the :ref:`ref-classes-cml1` class, to
     easily examine the final generated ``.config`` file.

-  New core recipes:

   -  `appstream <https://github.com/ximion/appstream>`__: a collaborative effort
      for making machine-readable software metadata easily available
      (from meta-oe)

   -  `cargo-c-native <https://crates.io/crates/cargo-c>`__: cargo applet to build
      and install C-ABI compatible dynamic and static libraries

   -  `libadwaita <https://gitlab.gnome.org/GNOME/libadwaita>`__: Building blocks
      for modern GNOME applications (from meta-gnome)

   -  `libtraceevent <https://git.kernel.org/pub/scm/libs/libtrace/libtracefs.git/>`__:
      API to access the kernel tracefs directory (from meta-openembedded)

   -  `libxmlb <https://github.com/hughsie/libxmlb>`__: A library to help create
      and query binary XML blobs (from meta-oe)

   -  ``musl-legacy-error``: glibc ``error()`` API implementation still needed
      by a few packages.

   -  `python3-beartype <https://beartype.readthedocs.io>`__, unbearably fast
      runtime type checking in pure Python.

   -  `python3-booleanpy <https://github.com/bastikr/boolean.py>`__: Define boolean
      algebras, create and parse boolean expressions and create custom boolean DSL
      (from meta-python)

   -  `python3-calver <https://github.com/di/calver>`__: Setuptools extension for
      CalVer package versions

   -  `python3-click <http://click.pocoo.org/>`__: A simple wrapper around optparse
      for powerful command line utilities (from meta-python)

   -  ``python3-dtc``: Python Library for the Device Tree Compiler (from
      meta-virtualization)

   -  `python3-isodate <https://github.com/gweis/isodate/>`__: ISO 8601 date/time
      parser (from meta-python)

   -  `python3-license-expression <https://github.com/nexB/license-expression>`__:
      Utility library to parse, compare, simplify and normalize license expressions
      (from meta-python)

   -  `python3-rdflib <https://github.com/RDFLib/rdflib>`__: a pure Python package
      for working with RDF (from meta-python)

   -  `python3-spdx-tools <https://github.com/spdx/tools-python>`__,
      tools for SPDX validation and conversion.

   -  `python3-trove-classifiers <https://github.com/pypa/trove-classifiers>`__:
      Canonical source for classifiers on PyPI (pypi.org)

   -  `python3-uritools <https://github.com/tkem/uritools/>`__, replacement for
      the ``urllib.parse`` module.

   -  `python3-xmltodict <https://github.com/martinblech/xmltodict>`__: Makes
      working with XML feel like you are working with JSON (from meta-python)

   -  `ttyrun <https://github.com/ibm-s390-linux/s390-tools>`__, starts
      ``getty`` programs only when a terminal exists, preventing respawns
      through the ``init`` program. This enabled removing the
      ``SERIAL_CONSOLES_CHECK`` variable.

   -  ``vulkan-validation-layers``: Khronos official validation layers to assist in
      verifying that applications correctly use the
      `Vulkan API <https://www.khronos.org/vulkan>`__.

   -  `xcb-util-cursor <http://xcb.freedesktop.org/XcbUtil/>`__: XCB port of
      libXcursor (from meta-oe)

-  QEMU / ``runqemu`` enhancements:

   -  QEMU has been upgraded to version 8.1

   -  Many updates to the ``runqemu`` command.

   -  The ``qemu-system-native`` recipe is now built with PNG support, which could be
      useful to grab screenshots for error reporting purposes.

-  Rust improvements:

   -  Rust has been upgraded to version 1.70

   -  New ``ptest-cargo`` class was added to allow Cargo based recipes to easily add ptests

   -  New :ref:`ref-classes-cargo_c` class was added to allow recipes to make Rust code
      available to C and C++ programs. See
      ``meta-selftest/recipes-devtools/rust/rust-c-lib-example_git.bb`` for an example.

-  wic Image Creator enhancements:

   -  ``bootimg-efi``: if ``fixed-size`` is set then use that for mkdosfs

   -  ``bootimg-efi``: stop hardcoding VMA offsets, as required by systemd-boot v254
      (and dracut/ukify)

   -  ``bootimg-pcbios``: use kernel name from :term:`KERNEL_IMAGETYPE` instead of
      hardcoding ``vmlinuz``

   -  Added new ``gpt-hybrid`` option to ``ptable_format`` (formatting a disk with a hybrid
      MBR and GPT partition scheme)

   -  Use ``part_name`` in default imager when defined

   -  Added ``--hidden`` argument to default imager to avoid MS Windows prompting to
      format partition after flashing to a USB stick/SD card

-  FIT image related improvements:

   -  New :term:`FIT_ADDRESS_CELLS` and :term:`UBOOT_FIT_ADDRESS_CELLS` variables allowing
      to specify 64 bit addresses, typically for loading U-Boot.

   -  Added ``compatible`` line to config section (with value from dtb) to allow bootloaders
      to select the best matching configuration.


-  SDK-related improvements:

   -  Extended the following recipes to ``nativesdk``: ``libwebp``, ``python3-ply``

-  Testing:

   -  The :ref:`ref-classes-insane` class now adds an :ref:`unimplemented-ptest
      <qa-check-unimplemented-ptest>` infrastructure to detect package sources
      with unit tests but no implemented ptests in the recipe.

   -  A new task to perform recipe-wide QA checks was added: ``do_recipe_qa``.

   -  New build-time checks for set :term:`SUMMARY`, :term:`HOMEPAGE`, and
      :term:`RECIPE_MAINTAINER` fields was added, and enabled for the core
      recipes.

   -  The ``parselogs`` runtime test was rewritten.  Notably it no longer uses
      regular expressions, which may mean custom patterns need updating.

   -  A self-test to validate that the :term:`SPDX` manifests generated by
      image builds are valid was added.

   -  The ``QEMU_USE_SLIRP`` variable has been replaced by adding ``slirp`` to
      ``TEST_RUNQEMUPARAMS``.

-  Utility script changes:

   -  New ``scripts/patchtest`` utility to check patches to the
      OpenEmbedded-Core project. See
      :ref:`contributor-guide/submit-changes:validating patches with patchtest`
      for details.

   -  ``scripts/bblock`` was added, allowing the user to lock/unlock specific
      recipes from being built. This makes it possibly to work on the
      ``python3`` recipe without causing ``python3-native`` to rebuild.

-  BitBake improvements:

   -  A fetcher for the Google Cloud Platform (``gs://``) was added.

   -  The BitBake Cooker log now contains notes when the caches are
      invalidated which is useful for memory resident BitBake debugging.

   -  BitBake no longer watches files with :wikipedia:`inotify <inotify>` for
      changes, as under load this can lead to races causing build instability.

   -  Toaster's dependencies were upgraded to current releases, specifically
      to Django 4.2.

-  Packaging changes:

   -  :term:`FILES` now accepts a ``**`` wildcard, which matches zero or more
      subdirectories.

   -  The X server packagegroup now defaults to using the ``modesetting`` X
      driver, which obsoletes the ``fbdev`` driver.

   -  If a recipe uses :term:`LICENSE_FLAGS` and the licenses are not accepted,
      it can set a custom message with :term:`LICENSE_FLAGS_DETAILS` to be
      displayed to the users.

   -  Recipes that fetch specific revisions no longer need to explicitly add
      :term:`SRCPV` to :term:`PV` as BitBake will now automatically add the
      revision information to :term:`PKGV` if needed (as long as "+" is still
      present in the :term:`PKGV` value, which is set from :term:`PV` by
      default).

   -  The default :term:`PR` values in many recipes have been removed.

-  Security improvements:

   -  Most repositories now include a :yocto_git:`SECURITY.md
      </poky/tree/SECURITY.md>` file with hints for security researchers
      and other parties who might report potential security vulnerabilities.

-  Prominent documentation updates:

   -  New :doc:`../contributor-guide/index` document.

   -  New :doc:`../dev-manual/security-subjects` chapter in the Development
      Tasks Manual.

   -  Long overdue documentation for the :ref:`ref-classes-devicetree` class.

   -  New :ref:`summary about available init systems
      <dev-manual/init-manager:summary>`.

   -  New documentation for the :ref:`ref-classes-uboot-sign` class and
      its variables and for the :ref:`ref-classes-kernel-devicetree` class
      variables.

-  Miscellaneous changes:

   -  Selecting systemd via :term:`INIT_MANAGER` now adds ``usrmerge`` to
      :term:`DISTRO_FEATURES` as current versions of systemd now require
      merged ``/usr``.

   -  Generation of :term:`SPDX` manifests is now enabled by default.

   -  Git based recipes in OE-Core which used the ``git``  protocol have been
      changed to use `https`` where possible, as it is typically faster and
      more reliable.

   -  The ``os-release`` recipe added a ``CPE_NAME`` to the fields provided, with the
      default being populated from :term:`DISTRO`.

   -  The ``psplash`` recipe now accepts a PNG format image through
      :term:`SPLASH_IMAGES`, instead of a harder to generate and modify
      ``.h`` file.

   -  The ; character is no longer needed to separate functions specified in
      :term:`IMAGE_POSTPROCESS_COMMAND`, :term:`IMAGE_PREPROCESS_COMMAND`,
      :term:`POPULATE_SDK_POST_HOST_COMMAND`, :term:`ROOTFS_POSTINSTALL_COMMAND`
      etc. (If any are present they will be replaced with spaces, so existing
      metadata does not yet need to be changed.)

   -  In the ``Upstream-Status`` field in a patch header, "Accepted" is no longer
      a valid value since it is logically the same as "Backport". Change any
      values you have (particularly in patches applied through bbappends for core
      recipes, since they will be validated as indicated above).


Known Issues in 4.3
~~~~~~~~~~~~~~~~~~~

-  N/A


Recipe License changes in 4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

-  ``glib-networking``: make :term:`LICENSE` more accurate (``LGPL-2.1`` -> ``LGPL-2.1-or-later``) and add an exception for linking to OpenSSL if it is enabled (``openssl`` is in :term:`PACKAGECONFIG`)
-  ``libbsd``: set per-package licensing to clarify that BSD-4-Clause code is only in the ``-doc`` package
-  ``openssh``: BSD-4-Clause code has been removed completely from the codebase as part of 9.4p1 update - previously in the kirkstone release, ``BSD-4-Clause`` was removed from the :term:`LICENSE` value in our recipe, however some BSD-4-Clause code actually still remained upstream until 9.4p1.
-  ``python3-sphinx``: remove ``BSD-3-Clause`` from :term:`LICENSE` - BSD-3-Clause code was removed as part of the python3-sphinx 7.0.1 release (see `this upstream commit <https://github.com/sphinx-doc/sphinx/commit/a7f5d91c29d6f377b9fe7e926965c6f9d3e7b802>`__)


Security Fixes in 4.3
~~~~~~~~~~~~~~~~~~~~~

-  bind: :cve_nist:`2023-2911`, :cve_nist:`2023-2828`, :cve_nist:`2023-3341`, :cve_nist:`2023-4236`
-  binutils: :cve_nist:`2023-1972`
-  connman: :cve_nist:`2023-28488`
-  cups: :cve_nist:`2023-32324`, :cve_nist:`2023-34241`, :cve_nist:`2023-4504`
-  dbus: :cve_nist:`2023-34969`
-  dmidecode: :cve_nist:`2023-30630`
-  dropbear: :cve_nist:`2023-36328`
-  erofs-utils: :cve_nist:`2023-33551`, :cve_nist:`2023-33552`
-  gcc: :cve_nist:`2023-4039`
-  ghostscript: :cve_nist:`2023-28879`, :cve_nist:`2023-36664`, :cve_nist:`2023-38559;` ignore :cve_nist:`2023-38560`
-  git: :cve_nist:`2023-25652`, :cve_nist:`2023-29007`
-  glibc: :cve_nist:`2023-4527`, :cve_nist:`2023-4806`
-  go: :cve_nist:`2023-24537`, :cve_nist:`2023-39325`
-  gstreamer: :cve_nist:`2023-40475`, :cve_nist:`2023-40476`
-  inetutils: :cve_nist:`2023-40303`
-  libarchive: ignore :cve_nist:`2023-30571`
-  librsvg: :cve_nist:`2023-38633`
-  libwebp: :cve_nist:`2023-1999`, :cve_nist:`2023-4863`
-  libx11: :cve_nist:`2023-3138`, :cve_nist:`2023-43785`, :cve_nist:`2023-43786`, :cve_nist:`2023-43787`
-  libxml2: :cve_nist:`2023-28484`, :cve_nist:`2023-29469;` ignore disputed :cve_nist:`2023-45322`
-  libxpm: :cve_nist:`2023-43788`, :cve_nist:`2023-43789`, :cve_nist:`2022-44617`
-  linux: update CVE exclusions
-  ncurses: :cve_nist:`2023-29491`
-  nghttp2: :cve_nist:`2023-44487`
-  ninja: ignore :cve_nist:`2021-4336`, wrong ninja
-  openssh: :cve_nist:`2023-38408`
-  openssl: :cve_nist:`2023-2650`, :cve_nist:`2023-1255`, :cve_nist:`2023-0466`, :cve_nist:`2023-0465`, :cve_nist:`2023-0464`, :cve_nist:`2023-3817`, :cve_nist:`2023-3446`, :cve_nist:`2023-2975`, :cve_nist:`2023-4807`
-  perl: :cve_nist:`2023-31484`, :cve_nist:`2023-31486`
-  pixman: ignore :cve_nist:`2023-37769`
-  procps: :cve_nist:`2023-4016`
-  python3-git: :cve_nist:`2023-41040`
-  python3: ignore :cve_nist:`2023-36632`
-  python3-urllib3: :cve_nist:`2023-43804`
-  qemu: :cve_nist:`2023-40360`, :cve_nist:`2023-42467;` ignore :cve_nist:`2023-0664` (Windows-specific), ignore :cve_nist:`2023-2680` (RHEL specific)
-  screen: :cve_nist:`2023-24626`
-  shadow: :cve_nist:`2023-29383`
-  sqlite3: ignore :cve_nist:`2023-36191`
-  sysstat: :cve_nist:`2023-33204`
-  tiff: :cve_nist:`2022-4645`, :cve_nist:`2023-2731`, :cve_nist:`2023-26965`, :cve_nist:`2023-40745`, :cve_nist:`2023-41175`
-  vim: :cve_nist:`2023-2426`, :cve_nist:`2023-2609`, :cve_nist:`2023-2610`, :cve_nist:`2023-3896`, :cve_nist:`2023-5441`, :cve_nist:`2023-5535`
-  zlib: ignore :cve_nist:`2023-45853`


Recipe Upgrades in 4.3
~~~~~~~~~~~~~~~~~~~~~~

-  acpica: upgrade 20220331 -> 20230628
-  adwaita-icon-theme: 43 -> 45.0
-  alsa-lib: upgrade 1.2.8 -> 1.2.10
-  alsa-ucm-conf: upgrade 1.2.8 -> 1.2.10
-  alsa-utils: upgrade 1.2.8 -> 1.2.10
-  apr: upgrade 1.7.2 -> 1.7.4
-  apt: Upgrade to v2.6.0
-  at-spi2-core: update 2.46.0 -> 2.50.0
-  autoconf: Upgrade to 2.72c
-  babeltrace2: upgrade 2.0.4 -> 2.0.5
-  bind: upgrade 9.18.12 -> 9.18.19
-  binutils: Upgrade to 2.41 release
-  bluez5: upgrade 5.66 -> 5.69
-  boost: upgrade 1.81.0 -> 1.83.0
-  btrfs-tools: upgrade 6.1.3 -> 6.5.1
-  busybox: 1.36.0 -> 1.36.1
-  ccache: upgrade 4.7.4 -> 4.8.3
-  cmake: upgrade to 3.27.5
-  connman: update 1.41 -> 1.42
-  coreutils: upgrade 9.1 -> 9.4
-  cpio: upgrade to 2.14
-  cracklib: upgrade 2.9.10 -> 2.9.11
-  createrepo-c: update 0.20.1 -> 1.0.0
-  cryptodev: update to 1.13 + latest git
-  cups: upgrade to 2.4.6
-  curl: upgrade 8.0.1 -> 8.4.0
-  dbus: upgrade 1.14.6 -> 1.14.10
-  debianutils: upgrade 5.8 -> 5.13
-  dhcpcd: upgrade to 10.0.2
-  diffoscope: upgrade 236 -> 249
-  diffutils: update 3.9 -> 3.10
-  dmidecode: upgrade to 3.5
-  dnf: upgrade 4.14.0 -> 4.17.0
-  dos2unix: upgrade 7.4.4 -> 7.5.1
-  dpkg: upgrade to v1.22.0
-  efivar: Upgrade to tip of trunk
-  elfutils: upgrade 0.188 -> 0.189
-  ell: upgrade 0.56 -> 0.58
-  enchant2: upgrade 2.3.4 -> 2.6.1
-  epiphany: upgrade 43.1 -> 44.6
-  erofs-utils: update 1.5 -> 1.6
-  ethtool: upgrade 6.2 -> 6.5
-  eudev: Upgrade 3.2.11 -> 3.2.12
-  ffmpeg: update 5.1.2 -> 6.0
-  file: upgrade 5.44 -> 5.45
-  flac: Upgrade 1.4.2 -> 1.4.3
-  font-util: upgrade 1.4.0 -> 1.4.1
-  freetype: upgrade 2.13.0 -> 2.13.2
-  fribidi: upgrade 1.0.12 -> 1.0.13
-  gawk: upgrade 5.2.1 -> 5.2.2
-  gcc: upgrade to 13.2
-  gcompat: Upgrade to 1.1.0
-  gcr: update 4.0.0 -> 4.1.0
-  gdb: upgrade 13.1 -> 13.2
-  gettext: upgrade 0.21.1 -> 0.22
-  ghostscript: upgrade to 10.02.0
-  git: upgrade to 2.42.0
-  glib-2.0: upgrade 2.74.6 -> 2.78.0
-  glibc: upgrade to 2.38 + stable updates
-  glib-networking: upgrade 2.74.0 -> 2.76.1
-  glslang: upgrade to 1.3.243
-  gmp: upgrade 6.2.1 -> 6.3.0
-  gnu-efi: upgrade 3.0.15 -> 3.0.17
-  gnupg: upgrade 2.4.0 -> 2.4.3
-  gnutls: update 3.8.0 -> 3.8.1
-  gobject-introspection: upgrade 1.74.0 -> 1.78.1
-  go-helloworld: Upgrade to tip of trunk
-  go: update 1.20.1 -> 1.20.10
-  gpgme: update 1.18.0 -> 1.22.0
-  grep: upgrade 3.10 -> 3.11
-  groff: update 1.22.4 -> 1.23.0
-  gsettings-desktop-schemas: upgrade 43.0 -> 44.0
-  gstreamer1.0: upgrade 1.22.0 -> 1.22.5
-  gstreamer: upgrade 1.22.5 -> 1.22.6
-  gtk+3: upgrade 3.24.36 -> 3.24.38
-  gtk4: update 4.10.0 -> 4.12.3
-  gzip: update 1.12 -> 1.13
-  harfbuzz: upgrade 7.1.0 -> 8.2.1
-  icu: upgrade 72-1 -> 73-2
-  igt-gpu-tools: update 1.27.1 -> 1.28
-  iproute2: upgrade 6.2.0 -> 6.5.0
-  iso-codes: upgrade 4.13.0 -> 4.15.0
-  jquery: upgrade 3.6.3 -> 3.7.1
-  json-c: upgrade 0.16 -> 0.17
-  kbd: upgrade 2.5.1 -> 2.6.3
-  kea: upgrade to v2.4.0
-  kexec-tools: upgrade 2.0.26 -> 2.0.27
-  kmscube: upgrade to latest revision
-  less: update 608 -> 643
-  libadwaita: upgrade 1.3.3 -> 1.4.0
-  libarchive: upgrade 3.6.2 -> 3.7.2
-  libassuan: upgrade 2.5.5 -> 2.5.6
-  libatomic-ops: update 7.6.14 -> 7.8.0
-  libcap: upgrade 2.67 -> 2.69
-  libcgroup: update 3.0.0 -> 3.1.0
-  libconvert-asn1-perl: upgrade 0.33 -> 0.34
-  libdnf: update 0.70.1 -> 0.70.1
-  libdrm: upgrade 2.4.115 -> 2.4.116
-  libedit: upgrade 20221030-3.1 -> 20230828-3.1
-  libevdev: upgrade 1.13.0 -> 1.13.1
-  libgcrypt: update 1.10.1 -> 1.10.2
-  libgit2: upgrade 1.6.3 -> 1.7.1
-  libglu: update 9.0.2 -> 9.0.3
-  libgpg-error: update 1.46 -> 1.47
-  libgudev: upgrade 237 -> 238
-  libhandy: upgrade 1.8.1 -> 1.8.2
-  libinput: upgrade to 1.24.0
-  libjpeg-turbo: upgrade to 3.0.0
-  libksba: upgrade 1.6.3 -> 1.6.4
-  libmd: upgrade 1.0.4 -> 1.1.0
-  libmicrohttpd: upgrade 0.9.76 -> 0.9.77
-  libmodule-build-perl: upgrade 0.4232 -> 0.4234
-  libmodulemd: upgrade 2.14.0 -> 2.15.0
-  libnl: upgrade 3.7.0 -> 3.8.0
-  libnss-nis: upgrade 3.1 -> 3.2
-  libpam: update 1.5.2 -> 1.5.3
-  libpcap: upgrade 1.10.3 -> 1.10.4
-  libpng: upgrade 1.6.39 -> 1.6.40
-  libportal: upgrade 0.6 -> 0.7.1
-  libproxy: update 0.4.18 -> 0.5.3
-  libpthread-stubs: update 0.4 -> 0.5
-  librepo: upgrade 1.15.1 -> 1.16.0
-  librsvf: update 2.54.5 -> 2.56.0
-  librsvg: update 2.56.0 -> 2.56.3
-  libsdl2: upgrade 2.26.3 -> 2.28.3
-  libsecret: upgrade 0.20.5 -> 0.21.1
-  libsndfile1: upgrade 1.2.0 -> 1.2.2
-  libsolv: upgrade 0.7.23 -> 0.7.25
-  libsoup: upgrade 3.2.2 -> 3.4.2
-  libssh2: update 1.10.0 -> 1.11.0
-  libtraceevent: upgrade 1.7.2 -> 1.7.3
-  libubootenv: upgrade 0.3.3 -> 0.3.4
-  liburi-perl: update 5.17 -> 5.21
-  libuv: upgrade 1.44.2 -> 1.46.0
-  libva: update 2.16 -> 2.19.0
-  libva-utils: update 2.19.0 -> 2.20.0
-  libwebp: upgrade 1.3.0 -> 1.3.2
-  libx11: upgrade 1.8.4 -> 1.8.7
-  libxcb: upgrade 1.15 -> 1.16
-  libxcrypt: upgrade 4.4.33 -> 4.4.36
-  libxfixes: Upgrade to v6.0.1
-  libxft: upgrade 2.3.7 -> 2.3.8
-  libxi: upgrade to v1.8.1
-  libxml2: upgrade 2.10.3 -> 2.11.5
-  libxpm: upgrade 3.5.15 -> 3.5.17
-  libxslt: upgrade 1.1.37 -> 1.1.38
-  libxt: Upgrade to v1.3.0
-  lighttpd: upgrade 1.4.69 -> 1.4.71
-  linux-firmware: upgrade 20230210 -> 20230804
-  linux-libc-headers: uprev to v6.5
-  linux-yocto/6.1: update to v6.1.57
-  linux-yocto-dev: update to v6.6-rcX
-  linux-yocto: introduce 6.5 reference kernel recipes
-  llvm: Upgrade to 17.0.2
-  ltp: upgrade 20230127 -> 20230516
-  lttng-modules: Upgrade 2.13.9 -> 2.13.10
-  lttng-tools: Upgrade 2.13.9 -> 2.13.11
-  lttng-ust: upgrade 2.13.5 -> 2.13.6
-  lua: update 5.4.4 -> 5.4.6
-  man-pages: upgrade 6.03 -> 6.05.01
-  mc: upgrade 4.8.29 -> 4.8.30
-  mesa: upgrade 23.0.0 -> 23.2.1
-  meson: upgrade 1.0.1 -> 1.2.2
-  mmc-utils: upgrade to latest revision
-  mobile-broadband-provider-info: upgrade 20221107 -> 20230416
-  mpfr: upgrade 4.2.0 -> 4.2.1
-  mpg123: upgrade 1.31.2 -> 1.31.3
-  msmtp: upgrade 1.8.23 -> 1.8.24
-  mtd-utils: upgrade 2.1.5 -> 2.1.6
-  mtools: upgrade 4.0.42 -> 4.0.43
-  musl: update to latest master
-  neard: upgrade 0.18 -> 0.19
-  nettle: upgrade 3.8.1 -> 3.9.1
-  nfs-utils: upgrade 2.6.2 -> 2.6.3
-  nghttp2: upgrade 1.52.0 -> 1.57.0
-  ofono: upgrade 2.0 -> 2.1
-  openssh: upgrade to 9.5p1
-  openssl: upgrade 3.1.0 -> 3.1.3
-  opkg: upgrade 0.6.1 -> 0.6.2
-  opkg-utils: upgrade 0.5.0 -> 0.6.2
-  orc: upgrade 0.4.33 -> 0.4.34
-  ovmf: update 202211 -> 202305
-  ovmf: update edk2-stable202305 -> edk2-stable202308
-  p11-kit: upgrade 0.24.1 -> 0.25.0
-  pango: upgrade 1.50.13 -> 1.51.0
-  parted: upgrade 3.5 -> 3.6
-  patchelf: Upgrade 0.17.2 -> 0.18.0
-  pciutils: upgrade 3.9.0 -> 3.10.0
-  perlcross: update 1.4 -> 1.5
-  perl: update 5.36.0 -> 5.38.0
-  piglit: upgrade to latest revision
-  pigz: upgrade 2.7 -> 2.8
-  pkgconf: upgrade 1.9.4 -> 2.0.3
-  ppp: upgrade 2.4.9 -> 2.5.0
-  procps: update 4.0.3 -> 4.0.4
-  puzzles: upgrade to latest revision
-  python3-attrs: upgrade 22.2.0 -> 23.1.0
-  python3-build: upgrade to 1.0.3
-  python3-certifi: upgrade 2022.12.7 -> 2023.7.22
-  python3-chardet: upgrade 5.1.0 -> 5.2.0
-  python3-cryptography{-vectors}: upgrade 39.0.2 -> 41.0.4
-  python3-cython: upgrade 0.29.33 -> 0.29.36
-  python3-dbusmock: upgrade 0.28.7 -> 0.29.1
-  python3-docutils: upgrade 0.19 -> 0.20.1
-  python3-dtc: upgrade 1.6.1 -> 1.7.0
-  python3-dtschema: upgrade 2023.1 -> 2023.7
-  python3-editables: upgrade 0.3 -> 0.5
-  python3-flit-core: upgrade 3.8.0 -> 3.9.0
-  python3-git: upgrade 3.1.31 -> 3.1.36
-  python3-hatch-fancy-pypi-readme: upgrade 22.8.0 -> 23.1.0
-  python3-hatchling: upgrade 1.13.0 -> 1.18.0
-  python3-hypothesis: upgrade 6.68.2 -> 6.86.2
-  python3-importlib-metadata: upgrade 6.0.0 -> 6.8.0
-  python3-installer: upgrade 0.6.0 -> 0.7.0
-  python3-iso8601: upgrade 1.1.0 -> 2.0.0
-  python3-jsonpointer: upgrade to 2.4
-  python3-libarchive-c: upgrade 4.0 -> 5.0
-  python3-lxml: upgrade 4.9.2 -> 4.9.3
-  python3-markdown: upgrade 3.4.1 -> 3.4.4
-  python3-markupsafe: upgrade 2.1.2 -> 2.1.3
-  python3-more-itertools: upgrade 9.1.0 -> 10.1.0
-  python3-numpy: upgrade 1.24.2 -> 1.26.0
-  python3-packaging: upgrade 23.0 -> 23.1
-  python3-pathspec: upgrade 0.11.0 -> 0.11.2
-  python3-pip: upgrade 23.0.1 -> 23.2.1
-  python3-pluggy: upgrade 1.0.0 -> 1.3.0
-  python3-poetry-core: upgrade 1.5.2 -> 1.7.0
-  python3-psutil: upgrade 5.9.4 -> 5.9.5
-  python3-pyasn1: upgrade 0.4.8 -> 0.5.0
-  python3-pycairo: upgrade 1.23.0 -> 1.24.0
-  python3-pycryptodome: upgrade 3.17 -> 3.19.0
-  python3-pycryptodomex: upgrade 3.17 -> 3.19.0
-  python3-pyelftools: upgrade 0.29 -> 0.30
-  python3-pygments: upgrade 2.14.0 -> 2.16.1
-  python3-pygobject: upgrade 3.42.2 -> 3.46.0
-  python3-pyopenssl: upgrade 23.0.0 -> 23.2.0
-  python3-pyparsing: upgrade 3.0.9 -> 3.1.1
-  python3-pytest-subtests: upgrade 0.10.0 -> 0.11.0
-  python3-pytest: upgrade 7.2.2 -> 7.4.2
-  python3-pytz: upgrade 2022.7.1 -> 2023.3
-  python3-pyyaml: upgrade 6.0 -> 6.0.1
-  python3-requests: Upgrade to 2.31.0
-  python3-ruamel-yaml: upgrade 0.17.21 -> 0.17.32
-  python3-setuptools-rust: upgrade 1.5.2 -> 1.7.0
-  python3-setuptools: upgrade 67.6.0 -> 68.2.2
-  python3-smmap: upgrade 5.0.0 -> 6.0.0
-  python3-sphinx-rtd-theme: upgrade 1.2.0 -> 1.3.0
-  python3-sphinx: upgrade 6.1.3 -> 7.2.6
-  python3-trove-classifiers: upgrade 2023.4.29 -> 2023.9.19
-  python3-typing-extensions: upgrade 4.5.0 -> 4.8.0
-  python3: upgrade 3.11.2 -> 3.11.5
-  python3-urllib3: upgrade 1.26.15 -> 2.0.6
-  python3-webcolors: upgrade 1.12 -> 1.13
-  python3-wheel: upgrade 0.40.0 -> 0.41.2
-  python3-zipp: upgrade 3.15.0 -> 3.17.0
-  qemu: Upgrade 7.2.0 -> 8.1.0
-  re2c: upgrade 3.0 -> 3.1
-  repo: upgrade 2.32 -> 2.36.1
-  rpcsvc-proto: Upgrade to 1.4.4
-  rpm2cpio.sh: update to the last 4.x version
-  rpm: update 4.18.0 -> 4.18.1
-  ruby: upgrade 3.2.1 -> 3.2.2
-  rust: Upgrade 1.68.1 -> 1.70.0
-  screen: update 4.9.0 -> 4.9.1
-  seatd: upgrade 0.7.0 -> 0.8.0
-  serf: upgrade 1.3.9 -> 1.3.10
-  shaderc: upgrade 2023.2 -> 2023.6
-  spirv-headers: upgrade 1.3.239.0 -> 1.3.243.0
-  spirv-tools: upgrade 1.3.239.0 -> 1.3.243.0
-  sqlite3: upgrade 3.41.0 -> 3.43.1
-  squashfs-tools: upgrade 4.5.1 -> 4.6.1
-  sstatesig: Update to match bitbake changes to runtaskdeps
-  strace: upgrade 6.2 -> 6.5
-  stress-ng: upgrade 0.15.06 -> 0.16.05
-  sudo: update 1.9.13p3 -> 1.9.14p3
-  sysfsutils: update 2.1.0 -> 2.1.1
-  sysklogd: upgrade 2.4.4 -> 2.5.2
-  sysstat: update 12.6.2 -> 12.7.4
-  systemd: upgrade 253.1 -> 254.4
-  systemtap: upgrade 4.8 -> 4.9
-  taglib: upgrade 1.13 -> 1.13.1
-  tar: upgrade 1.34 -> 1.35
-  tcf-agent: Update to 1.8.0 release
-  texinfo: upgrade 7.0.2 -> 7.0.3
-  tiff: upgrade to 4.6.0
-  u-boot: Upgrade to 2023.10
-  util-linux: upgrade 2.38.1 -> 2.39.2
-  vala: upgrade 0.56.4 -> 0.56.13
-  valgrind: update 3.20.0 -> 3.21.0
-  vim: upgrade 9.0.1429 -> 9.0.2048
-  vte: upgrade 0.72.0 -> 0.72.2
-  vulkan-headers: upgrade to 1.3.243
-  vulkan-loader: upgrade to 1.3.243
-  vulkan-samples: update to latest SHA
-  vulkan-tools: upgrade to 1.3.243
-  vulkan: upgrade 1.3.243.0 -> 1.3.261.1
-  waffle: upgrade 1.7.0 -> 1.7.2
-  wayland-protocols: upgrade 1.31 -> 1.32
-  wayland: upgrade 1.21.0 -> 1.22.0
-  wayland-utils: upgrade 1.1.0 -> 1.2.0
-  webkitgtk: update 2.38.5 -> 2.40.5
-  weston: update 11.0.1 -> 12.0.2
-  wget: upgrade 1.21.3 -> 1.21.4
-  wireless-regdb: upgrade 2023.02.13 -> 2023.09.01
-  wpebackend-fdo: upgrade 1.14.0 -> 1.14.2
-  xcb-proto: upgrade 1.15.2 -> 1.16.0
-  xdpyinfo: upgrade 1.3.3 -> 1.3.4
-  xeyes: upgrade 1.2.0 -> 1.3.0
-  xf86-input-libinput: upgrade 1.2.1 -> 1.4.0
-  xf86-input-mouse: upgrade 1.9.4 -> 1.9.5
-  xinput: upgrade to v1.6.4
-  xkeyboard-config: upgrade 2.38 -> 2.39
-  xorgproto: upgrade 2022.2 -> 2023.2
-  xserver-xorg: upgrade 21.1.7 -> 21.1.8
-  xtrans: update 1.4.0 -> 1.5.0
-  xwayland: upgrade 22.1.8 -> 23.2.1
-  xwininfo: upgrade to v1.1.6
-  xxhash: upgrade 0.8.1 -> 0.8.2
-  xz: upgrade 5.4.2 -> 5.4.4
-  zlib: upgrade 1.2.13 -> 1.3
-  zstd: upgrade 1.5.4 -> 1.5.5




Contributors to 4.3
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adrian Freihofer
-  Alassane Yattara
-  Alberto Pianon
-  Alberto Planas
-  Alejandro Hernandez Samaniego
-  Alexander Kanavin
-  Alexandre Belloni
-  Alexis Lothoré
-  Alex Kiernan
-  Andreas Cord-Landwehr
-  André Draszik
-  Andrej Valek
-  Andrew Jeffery
-  Andrey Zhizhikin
-  Angelo Ribeiro
-  Antoine Lubineau
-  Antonin Godard
-  Anuj Mittal
-  Archana Polampalli
-  Armin Kuster
-  Arne Schwerdt
-  Arno Baumfalk
-  Arslan Ahmad
-  Bartosz Golaszewski
-  BELHADJ SALEM Talel
-  BELOUARGA Mohamed
-  Benjamin Bara
-  Benjamin Bouvier
-  Bergin, Peter
-  Bruce Ashfield
-  Changhyeok Bae
-  Changqing Li
-  Charles-Antoine Couret
-  Charlie Wu
-  Chen Qi
-  Chi Xu
-  Chris Laplante
-  Christopher Larson
-  Daniel Ammann
-  Daniel McGregor
-  Daniel Semkowicz
-  David Reyna
-  Deepthi Hemraj
-  Denis OSTERLAND-HEIM
-  Denys Dmytriyenko
-  Derek Straka
-  Dit Kozmaj
-  Dmitry Baryshkov
-  Ed Beroset
-  Eero Aaltonen
-  Eilís 'pidge' Ní Fhlannagáin
-  Emil Ekmečić
-  Emil Kronborg Andersen
-  Enrico Jörns
-  Enrico Scholz
-  Etienne Cordonnier
-  Fabien Mahot
-  Fabio Estevam
-  Fahad Arslan
-  Frank WOLFF
-  Frederic Martinsons
-  Frieder Paape
-  Frieder Schrempf
-  Geoff Parker
-  Hannu Lounento
-  Ian Ray
-  Insu Park
-  Jaeyoon Jung
-  Jamin Lin
-  Jan Garcia
-  Jan Vermaete
-  Jasper Orschulko
-  Jean-Marie Lemetayer
-  Jérémy Rosen
-  Jermain Horsman
-  Jialing Zhang
-  Joel Stanley
-  Joe Slater
-  Johannes Schrimpf
-  Jon Mason
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Kai Kang
-  Khem Raj
-  Kyle Russell
-  Lee Chee Yang
-  Lei Maohui
-  Leon Anavi
-  Lorenzo Arena
-  Louis Rannou
-  Luan Rafael Carneiro
-  Luca Boccassi
-  Luca Ceresoli
-  Marc Ferland
-  Marcus Flyckt
-  Marek Vasut
-  Mark Asselstine
-  Mark Hatle
-  Markus Niebel
-  Markus Volk
-  Marlon Rodriguez Garcia
-  Marta Rybczynska
-  Martijn de Gouw
-  Martin Jansa
-  Martin Siegumfeldt
-  Matthias Schnelte
-  Mauro Queiros
-  Max Krummenacher
-  Michael Halstead
-  Michael Opdenacker
-  Mickael RAMILISON
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Narpat Mali
-  Natasha Bailey
-  Nikhil R
-  Ninad Palsule
-  Ola x Nilsson
-  Oleksandr Hnatiuk
-  Otavio Salvador
-  Ovidiu Panait
-  Pascal Bach
-  Patrick Williams
-  Paul Eggleton
-  Paul Gortmaker
-  Paulo Neves
-  Pavel Zhukov
-  Pawan Badganchi
-  Peter Bergin
-  Peter Hoyes
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Suti
-  Petr Gotthard
-  Petr Kubizňák
-  Piotr Łobacz
-  Poonam Jadhav
-  Qiu Tingting
-  Quentin Schulz
-  Randolph Sapp
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Rasmus Villemoes
-  Remi Peuvergne
-  Richard Purdie
-  Riyaz Khan
-  Robert Joslyn
-  Robert P. J. Day
-  Robert Yang
-  Roland Hieber
-  Ross Burton
-  Ryan Eatmon
-  Sakib Sajal
-  Samantha Jalabert
-  Sanjay Chitroda
-  Sean Nyekjaer
-  Sergei Zhmylev
-  Siddharth Doshi
-  Soumya Sambu
-  Staffan Rydén
-  Stefano Babic
-  Stefan Tauner
-  Stéphane Veyret
-  Stephan Wurm
-  Sudip Mukherjee
-  Sundeep KOKKONDA
-  Svend Meyland Nicolaisen
-  Tan Wen Yan
-  Thomas Roos
-  Tim Orling
-  Tom Hochstein
-  Tom Isaacson
-  Trevor Gamblin
-  Ulrich Ölmann
-  Victor Kamensky
-  Vincent Davis Jr
-  Virendra Thakur
-  Wang Mingyu
-  Xiangyu Chen
-  Yang Xu
-  Yash Shinde
-  Yi Zhao
-  Yoann Congal
-  Yogita Urade
-  Yuta Hayama
-  Zang Ruochen
-  Zhixiong Chi


Repositories / Downloads for Yocto-4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`nanbield </poky/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3 </poky/log/?h=yocto-4.3>`
-  Git Revision: :yocto_git:`15b576c4101231d248fda7ae0824e1780e1a8901 </poky/commit/?id=15b576c4101231d248fda7ae0824e1780e1a8901>`
-  Release Artefact: poky-15b576c4101231d248fda7ae0824e1780e1a8901
-  sha: 6b0ef7914d15db057f3efdf091b169a7361c74aac0abcfa717ef55d1a0adf74c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3/poky-15b576c4101231d248fda7ae0824e1780e1a8901.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3/poky-15b576c4101231d248fda7ae0824e1780e1a8901.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`nanbield </openembedded-core/log/?h=nanbield>`
-  Tag:  :oe_git:`yocto-4.3 </openembedded-core/log/?h=yocto-4.3>`
-  Git Revision: :oe_git:`4c261f8cbdf0c7196a74daad041d04eb093015f3 </openembedded-core/commit/?id=4c261f8cbdf0c7196a74daad041d04eb093015f3>`
-  Release Artefact: oecore-4c261f8cbdf0c7196a74daad041d04eb093015f3
-  sha: c9e6ac75d7848ce8844cb29c98659dd8f83b3de13b916124dff76abe034e6a5c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3/oecore-4c261f8cbdf0c7196a74daad041d04eb093015f3.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3/oecore-4c261f8cbdf0c7196a74daad041d04eb093015f3.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`nanbield </meta-mingw/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3 </meta-mingw/log/?h=yocto-4.3>`
-  Git Revision: :yocto_git:`65ef95a74f6ae815f63f636ed53e140a26a014ce </meta-mingw/commit/?id=65ef95a74f6ae815f63f636ed53e140a26a014ce>`
-  Release Artefact: meta-mingw-65ef95a74f6ae815f63f636ed53e140a26a014ce
-  sha: fb2bf806941a00a1be6349c074379b63a76490bcf0f3b740d96d1aeeefa12286
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3/meta-mingw-65ef95a74f6ae815f63f636ed53e140a26a014ce.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3/meta-mingw-65ef95a74f6ae815f63f636ed53e140a26a014ce.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.6 </bitbake/log/?h=2.6>`
-  Tag:  :oe_git:`yocto-4.3 </bitbake/log/?h=yocto-4.3>`
-  Git Revision: :oe_git:`5419a8473d6d4cd1d01537de68ad8d72cf5be0b2 </bitbake/commit/?id=5419a8473d6d4cd1d01537de68ad8d72cf5be0b2>`
-  Release Artefact: bitbake-5419a8473d6d4cd1d01537de68ad8d72cf5be0b2
-  sha: e5dab4b3345d91307860803e2ad73b2fcffa9d17dd3fde0e013ca0ebea0d05ca
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3/bitbake-5419a8473d6d4cd1d01537de68ad8d72cf5be0b2.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3/bitbake-5419a8473d6d4cd1d01537de68ad8d72cf5be0b2.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`nanbield </yocto-docs/log/?h=nanbield>`
-  Tag: :yocto_git:`yocto-4.3 </yocto-docs/log/?h=yocto-4.3>`
-  Git Revision: :yocto_git:`ceb1812e63b9fac062f886c2a1dde23137c0e1ed </yocto-docs/commit/?id=ceb1812e63b9fac062f886c2a1dde23137c0e1ed>`

