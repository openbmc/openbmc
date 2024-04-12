.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 4.0 (kirkstone)
---------------------------------

This is a Long Term Support release, published in April 2022, and supported at least for two years (April 2024).

New Features / Enhancements in 4.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- Linux kernel 5.15, glibc 2.35 and ~300 other recipe upgrades

- Reproducibility: this release fixes the reproducibility issues with
  ``rust-llvm`` and ``golang``. Recipes in OpenEmbedded-Core are now fully
  reproducible. Functionality previously in the optional "reproducible"
  class has been merged into the :ref:`ref-classes-base` class.

- Network access is now disabled by default for tasks other than where it is expected to ensure build integrity (where host kernel supports it)

- The Yocto Project now allows you to reuse the Shared State cache from
  its autobuilder. If the network connection between our server and your
  machine is faster than you would build recipes from source, you can
  try to speed up your builds by using such Shared State and Hash
  Equivalence by setting::

     BB_SIGNATURE_HANDLER = "OEEquivHash"
     BB_HASHSERVE = "auto"
     BB_HASHSERVE_UPSTREAM = "hashserv.yocto.io:8687"
     SSTATE_MIRRORS ?= "file://.* https://sstate.yoctoproject.org/all/PATH;downloadfilename=PATH"

- The Python package build process is now based on `wheels <https://pythonwheels.com/>`__
  in line with the upstream direction.

- New :ref:`ref-classes-overlayfs` and :ref:`ref-classes-overlayfs-etc` classes and
  ``overlayroot`` support in the :term:`Initramfs` framework to make it easier to
  overlay read-only filesystems (for example) with
  :wikipedia:`OverlayFS <OverlayFS>`.

- Inclusive language adjustments to some variable names - see the
  :ref:`4.0 migration guide <migration-4.0-inclusive-language>` for details.

- New recipes:

   - ``buildtools-docs-tarball``
   - ``libptytty``
   - ``libxcvt``
   - ``lua``
   - ``nghttp2``
   - ``python3-alabaster``
   - ``python3-asn1crypto``
   - ``python3-babel``
   - ``python3-bcrypt``
   - ``python3-certifi``
   - ``python3-cffi``
   - ``python3-chardet``
   - ``python3-cryptography``
   - ``python3-cryptography-vectors``
   - ``python3-dtschema``
   - ``python3-flit-core``
   - ``python3-idna``
   - ``python3-imagesize``
   - ``python3-installer``
   - ``python3-iso8601``
   - ``python3-jsonpointer``
   - ``python3-jsonschema``
   - ``python3-ndg-httpsclient``
   - ``python3-ply``
   - ``python3-poetry-core``
   - ``python3-pretend``
   - ``python3-psutil``
   - ``python3-pyasn1``
   - ``python3-pycparser``
   - ``python3-pyopenssl``
   - ``python3-pyrsistent``
   - ``python3-pysocks``
   - ``python3-pytest-runner``
   - ``python3-pytest-subtests``
   - ``python3-pytz``
   - ``python3-requests``
   - ``python3-rfc3339-validator``
   - ``python3-rfc3986-validator``
   - ``python3-rfc3987``
   - ``python3-ruamel-yaml``
   - ``python3-semantic-version``
   - ``python3-setuptools-rust-native``
   - ``python3-snowballstemmer``
   - ``python3-sphinx``
   - ``python3-sphinxcontrib-applehelp``
   - ``python3-sphinxcontrib-devhelp``
   - ``python3-sphinxcontrib-htmlhelp``
   - ``python3-sphinxcontrib-jsmath``
   - ``python3-sphinxcontrib-qthelp``
   - ``python3-sphinxcontrib-serializinghtml``
   - ``python3-sphinx-rtd-theme``
   - ``python3-strict-rfc3339``
   - ``python3-tomli``
   - ``python3-typing-extensions``
   - ``python3-urllib3``
   - ``python3-vcversioner``
   - ``python3-webcolors``
   - ``python3-wheel``
   - ``repo``
   - ``seatd``

- Extended recipes to ``native``: ``wayland``, ``wayland-protocols``

- Shared state (sstate) improvements:

   - Switched to :wikipedia:`ZStandard (zstd) <Zstd>` instead
     of Gzip, for better performance.
   - Allow validation of sstate signatures against a list of keys
   - Improved error messages and exception handling

- BitBake enhancements:

   - Fetcher enhancements:

      - New :ref:`bitbake-user-manual/bitbake-user-manual-fetching:crate fetcher (\`\`crate://\`\`)` for Rust packages
      - Added striplevel support to unpack
      - git: Add a warning asking users to set a branch in git urls
      - git: Allow git fetcher to support subdir param
      - git: canonicalize ids in generated tarballs
      - git: stop generated tarballs from leaking info
      - npm: Put all downloaded files in the npm2 directory
      - npmsw: Add support for duplicate dependencies without url
      - npmsw: Add support for github prefix in npm shrinkwrap version
      - ssh: now supports checkstatus, allows : in URLs (both required for use with sstate) and no longer requires username
      - wget: add redirectauth parameter
      - wget: add 30s timeout for checkstatus calls

   - Show warnings for append/prepend/remove operators combined with +=/.=
   - Add bb.warnonce() and bb.erroronce() log methods
   - Improved setscene task display
   - Show elapsed time also for tasks with progress bars
   - Improved cleanup on forced shutdown (either because of errors or Ctrl+C)
   - contrib: Add Dockerfile for building PR service container
   - Change file format of siginfo files to use zstd compressed json
   - Display active tasks when printing keep-alive message to help debugging

-  Architecture-specific enhancements:

   - ARM:

      - tune-cortexa72: Enable the crc extension by default for cortexa72
      - qemuarm64: Add tiny ktype to qemuarm64 bsp
      - armv9a/tune: Add the support for the Neoverse N2 core
      - arch-armv8-5a.inc: Add tune include for armv8.5a
      - grub-efi: Add xen_boot support when 'xen' is in :term:`DISTRO_FEATURES` for aarch64
      - tune-cortexa73: Introduce cortexa73-crypto tune
      - libacpi: Build libacpi also for 'aarch64' machines
      - core-image-tiny-initramfs: Mark recipe as 32 bit ARM compatible

   - PowerPC:

      - weston-init: Use pixman rendering for qemuppc64
      - rust: add support for big endian 64-bit PowerPC
      - rust: Add snapshot checksums for powerpc64le

   - RISC-V:

      - libunwind: Enable for rv64
      - systemtap: Enable for riscv64
      - linux-yocto-dev: add qemuriscv32
      - packagegroup-core-tools-profile: Enable systemtap for riscv64
      - qemuriscv: Use virtio-tablet-pci for mouse

   - x86:

      - kernel-yocto: conditionally enable stack protection checking on x86-64

-  Kernel-related enhancements:

   - Allow :term:`Initramfs` to be built from a separate multiconfig
   - Make kernel-base recommend kernel-image, not depend (allowing images containing kernel modules without kernel image)
   - linux-yocto: split vtpm for more granular inclusion
   - linux-yocto: cfg/debug: add configs for kcsan
   - linux-yocto: cfg: add kcov feature fragment
   - linux-yocto: export pkgconfig variables to devshell
   - linux-yocto-dev: use versioned branch as default
   - New :term:`KERNEL_DEBUG_TIMESTAMPS` variable (to replace removed ``BUILD_REPRODUCIBLE_BINARIES`` for the kernel)
   - Introduce python3-dtschema-wrapper in preparation for mandatory schema checking on dtb files in 5.16
   - Allow disabling kernel artifact symlink creation
   - Allow changing default .bin kernel artifact extension

- FIT image related enhancements:

   - New ``FIT_SUPPORTED_INITRAMFS_FSTYPES`` variable to allow extending :term:`Initramfs` image types to look for
   - New ``FIT_CONF_PREFIX`` variable to allow overriding FIT configuration prefix
   - Use 'bbnote' for better logging

- New :term:`PACKAGECONFIG` options in ``curl``, ``dtc``, ``epiphany``, ``git``, ``git``, ``gstreamer1.0-plugins-bad``, ``linux-yocto-dev``, ``kmod``, ``mesa``, ``piglit``, ``qemu``, ``rpm``, ``systemd``, ``webkitgtk``, ``weston-init``
- ptest enhancements in ``findutils``, ``lttng-tools``, ``openssl``, ``gawk``, ``strace``, ``lttng-tools``, ``valgrind``, ``perl``, ``libxml-parser-perl``, ``openssh``, ``python3-cryptography``, ``popt``

- Sysroot dependencies have been further optimised
- Significant effort to upstream / rationalise patches across a variety of recipes
- Allow the creation of block devices on top of UBI volumes
- archiver: new ARCHIVER_MODE[compression] to set tarball compression, and switch default to xz
- yocto-check-layer: add ability to perform tests from a global bbclass
- yocto-check-layer: improved README checks
- cve-check: add json output format
- cve-check: add coverage statistics on recipes with/without CVEs
- Added mirrors for kernel sources and uninative binaries on kernel.org
- glibc and binutils recipes now use shallow mirror tarballs for faster fetching
- When patching fails, show more information on the fatal error

-  wic Image Creator enhancements:

  - Support rootdev identified by partition label
  - rawcopy: Add support for packed images
  - partition: Support valueless keys in sourceparams

- QA check enhancements:

   - Allow treating license issues as errors
   - Added a check that Upstream-Status patch tag is present and correctly formed
   - Added a check for directories that are expected to be empty
   - Ensure addition of patch-fuzz retriggers do_qa_patch
   - Added a sanity check for allarch packagegroups

- :ref:`ref-classes-create-spdx` class improvements:

   - Get SPDX-License-Identifier from source files
   - Generate manifest also for SDKs
   - New SPDX_ORG variable to allow changing the Organization field value
   - Added packageSupplier field
   - Added create_annotation function

- devtool add / recipetool create enhancements:

   - Extend curl detection when creating recipes
   - Handle GitLab URLs like we do GitHub
   - Recognize more standard license text variants
   - Separate licenses with & operator
   - Detect more known licenses in Python code
   - Move license md5sums data into CSV files
   - npm: Use README as license fallback

- SDK-related enhancements:

   - Extended recipes to :ref:`ref-classes-nativesdk`: ``cargo``,
     ``librsvg``, ``libstd-rs``, ``libva``, ``python3-docutil``, ``python3-packaging``
   - Enabled :ref:`ref-classes-nativesdk` recipes to find a correct version
     of the rust cross compiler
   - Support creating per-toolchain cmake file in SDK

- Rust enhancements:

   - New python_setuptools3_rust class to enable building python extensions in Rust
   - classes/meson: Add optional rust definitions

- QEMU / runqemu enhancements:

   - qemu: Add knob for enabling PMDK pmem support
   - qemu: add tpm string section to qemu acpi table
   - qemu: Build on musl targets
   - runqemu: support rootfs mounted ro
   - runqemu: add :term:`DEPLOY_DIR_IMAGE` replacement in QB_OPT_APPEND
   - runqemu: Allow auto-detection of the correct graphics options

- Capped ``cpu_count()`` (used to set parallelisation defaults) to 64 since any higher usually hurts parallelisation
- Adjust some GL-using recipes so that they only require virtual/egl
- package_rpm: use zstd instead of xz
- npm: new ``EXTRA_OENPM`` variable (to set node-gyp variables for example)
- npm: new ``NPM_NODEDIR`` variable
- perl: Enable threading
- u-boot: Convert ${UBOOT_ENV}.cmd into ${UBOOT_ENV}.scr
- u-boot: Split do_configure logic into separate file
- go.bbclass: Allow adding parameters to go ldflags
- go: log build id computations
- scons: support out-of-tree builds
- scripts: Add a conversion script to use SPDX license names
- scripts: Add convert-variable-renames script for inclusive language variable renaming
- binutils-cross-canadian: enable gold for mingw
- grub-efi: Add option to include all available modules
- bitbake.conf: allow wayland distro feature through for native/SDK builds
- weston-init: Pass --continue-without-input when launching weston
- weston: wrapper for weston modules argument
- weston: Add a knob to control simple clients
- uninative: Add version to uninative tarball name
- volatile-binds: SELinux and overlayfs extensions in mount-copybind
- gtk-icon-cache: Allow using gtk4
- kmod: Add an exclude directive to depmod
- os-release: add os-release-initrd package for use in systemd-based :term:`Initramfs` images
- gstreamer1.0-plugins-base: add support for graphene
- gpg-sign: Add parameters to gpg signature function
- package_manager: sign DEB package feeds
- zstd: add libzstd package
- libical: build gobject and vala introspection
- dhcpcd: add option to set DBDIR location
- rpcbind: install rpcbind.conf
- mdadm: install mdcheck
- boost: add json lib
- libxkbcommon: allow building of API documentation
- libxkbcommon: split libraries and xkbcli into separate packages
- systemd: move systemd shared library into its own package
- systemd: Minimize udev package size if :term:`DISTRO_FEATURES` doen't contain sysvinit

Known Issues in 4.0
~~~~~~~~~~~~~~~~~~~

- ``make`` version 4.2.1 is known to be buggy on non-Ubuntu systems. If this ``make``
  version is detected on host distributions other than Ubuntu at build start time,
  then a warning will be displayed.

Recipe License changes in 4.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

* cmake: add BSD-1-Clause & MIT & BSD-2-Clause to :term:`LICENSE` due to additional vendored libraries in native/target context
* gettext: extend :term:`LICENSE` conditional upon :term:`PACKAGECONFIG` (due to vendored libraries)
* gstreamer1.0: update licenses of all modules to LGPL-2.1-or-later (with some exceptions that are GPL-2.0-or-later)
* gstreamer1.0-plugins-bad/ugly: use the GPL-2.0-or-later only when it is in use
* kern-tools-native: add missing MIT license due to Kconfiglib
* libcap: add pam_cap license to :term:`LIC_FILES_CHKSUM` if pam is enabled
* libidn2: add Unicode-DFS-2016 license
* libsdl2: add BSD-2-Clause to :term:`LICENSE` due to default yuv2rgb and hidapi inclusion
* libx11-compose-data: update :term:`LICENSE` to "MIT & MIT-style & BSD-1-Clause & HPND & HPND-sell-variant" to better reflect reality
* libx11: update :term:`LICENSE` to "MIT & MIT-style & BSD-1-Clause & HPND & HPND-sell-variant" to better reflect reality
* libxshmfence: correct :term:`LICENSE` - MIT -> HPND
* newlib: add BSD-3-Clause to :term:`LICENSE`
* python3-idna: correct :term:`LICENSE` - Unicode -> Unicode-TOU
* python3-pip: add "Apache-2.0 & MPL-2.0 & LGPL-2.1-only & BSD-3-Clause & PSF-2.0 & BSD-2-Clause" to :term:`LICENSE` due to vendored libraries

Other license-related notes:

- The ambiguous "BSD" license has been removed from the ``common-licenses`` directory.
  Each recipe that fetches or builds BSD-licensed code should specify the proper
  version of the BSD license in its :term:`LICENSE` value.

- :term:`LICENSE` definitions now have to use `SPDX identifiers <https://spdx.org/licenses/>`__.
  A :oe_git:`convert-spdx-licenses.py </openembedded-core/tree/scripts/contrib/convert-spdx-licenses.py>`
  script can be used to update your recipes.



Security Fixes in 4.0
~~~~~~~~~~~~~~~~~~~~~

- binutils: :cve:`2021-42574`, :cve:`2021-45078`
- curl: :cve:`2021-22945`, :cve:`2021-22946`, :cve:`2021-22947`
- epiphany: :cve:`2021-45085`, :cve:`2021-45086`, :cve:`2021-45087`, :cve:`2021-45088`
- expat: :cve:`2021-45960`, :cve:`2021-46143`, :cve:`2022-22822`, :cve:`2022-22823`, :cve:`2022-22824`, :cve:`2022-22825`, :cve:`2022-22826`, :cve:`2022-22827`, :cve:`2022-23852`, :cve:`2022-23990`, :cve:`2022-25235`, :cve:`2022-25236`, :cve:`2022-25313`, :cve:`2022-25314`, :cve:`2022-25315`
- ffmpeg: :cve:`2021-38114`
- gcc: :cve:`2021-35465`, :cve:`2021-42574`, :cve:`2021-46195`, :cve:`2022-24765`
- glibc: :cve:`2021-3998`, :cve:`2021-3999`, :cve:`2021-43396`, :cve:`2022-23218`, :cve:`2022-23219`
- gmp: :cve:`2021-43618`
- go: :cve:`2021-41771` and :cve:`2021-41772`
- grub2: :cve:`2021-3981`
- gzip: :cve:`2022-1271`
- libarchive : :cve:`2021-31566`, :cve:`2021-36976`
- libxml2: :cve:`2022-23308`
- libxslt: :cve:`2021-30560`
- lighttpd: :cve:`2022-22707`
- linux-yocto/5.10: amdgpu: :cve:`2021-42327`
- lua: :cve:`2021-43396`
- openssl: :cve:`2021-4044`, :cve:`2022-0778`
- qemu: :cve:`2022-1050`, :cve:`2022-26353`, :cve:`2022-26354`
- rpm: :cve:`2021-3521`
- seatd: :cve:`2022-25643`
- speex: :cve:`2020-23903`
- squashfs-tools: :cve:`2021-41072`
- systemd: :cve:`2021-4034`
- tiff: :cve:`2022-0561`, :cve:`2022-0562`, :cve:`2022-0865`, :cve:`2022-0891`, :cve:`2022-0907`, :cve:`2022-0908`, :cve:`2022-0909`, :cve:`2022-0924`, :cve:`2022-1056`, :cve:`2022-22844`
- unzip: :cve:`2021-4217`
- vim: :cve:`2021-3796`, :cve:`2021-3872`, :cve:`2021-3875`, :cve:`2021-3927`, :cve:`2021-3928`, :cve:`2021-3968`, :cve:`2021-3973`, :cve:`2021-4187`, :cve:`2022-0128`, :cve:`2022-0156`, :cve:`2022-0158`, :cve:`2022-0261`, :cve:`2022-0318`, :cve:`2022-0319`, :cve:`2022-0554`, :cve:`2022-0696`, :cve:`2022-0714`, :cve:`2022-0729`, :cve:`2022-0943`
- virglrenderer: :cve:`2022-0135`, :cve:`2022-0175`
- webkitgtk: :cve:`2022-22589`, :cve:`2022-22590`, :cve:`2022-22592`
- xz: :cve:`2022-1271`
- zlib: :cve:`2018-25032`



Recipe Upgrades in 4.0
~~~~~~~~~~~~~~~~~~~~~~

- acpica: upgrade 20210730 -> 20211217
- acpid: upgrade 2.0.32 -> 2.0.33
- adwaita-icon-theme: update 3.34/38 -> 41.0
- alsa-ucm-conf: upgrade 1.2.6.2 -> 1.2.6.3
- alsa: upgrade 1.2.5 -> 1.2.6
- apt: upgrade 2.2.4 -> 2.4.3
- asciidoc: upgrade 9.1.0 -> 10.0.0
- atk: upgrade 2.36.0 -> 2.38.0
- at-spi2-core: upgrade 2.40.3 -> 2.42.0
- at: update 3.2.2 -> 3.2.5
- autoconf-archive: upgrade 2021.02.19 -> 2022.02.11
- automake: update 1.16.3 -> 1.16.5
- bash: upgrade 5.1.8 -> 5.1.16
- bind: upgrade 9.16.20 -> 9.18.1
- binutils: Bump to latest 2.38 release branch
- bison: upgrade 3.7.6 -> 3.8.2
- bluez5: upgrade 5.61 -> 5.64
- boost: update 1.77.0 -> 1.78.0
- btrfs-tools: upgrade 5.13.1 -> 5.16.2
- buildtools-installer: Update to use 3.4
- busybox: 1.34.0 -> 1.35.0
- ca-certificates: update 20210119 -> 20211016
- cantarell-fonts: update 0.301 -> 0.303.1
- ccache: upgrade 4.4 -> 4.6
- cmake: update 3.21.1 -> 3.22.3
- connman: update 1.40 -> 1.41
- coreutils: update 8.32 -> 9.0
- cracklib: update 2.9.5 -> 2.9.7
- createrepo-c: upgrade 0.17.4 -> 0.19.0
- cronie: upgrade 1.5.7 -> 1.6.0
- cups: update 2.3.3op2 -> 2.4.1
- curl: update 7.78.0 -> 7.82.0
- dbus: upgrade 1.12.20 -> 1.14.0
- debianutils: update 4.11.2 -> 5.7
- dhcpcd: upgrade 9.4.0 -> 9.4.1
- diffoscope: upgrade 181 -> 208
- dnf: upgrade 4.8.0 -> 4.11.1
- dpkg: update 1.20.9 ->  1.21.4
- e2fsprogs: upgrade 1.46.4 -> 1.46.5
- ed: upgrade 1.17 -> 1.18
- efivar: update 37 -> 38
- elfutils: update 0.185 -> 0.186
- ell: upgrade 0.43 -> 0.49
- enchant2: upgrade 2.3.1 -> 2.3.2
- epiphany: update 40.3 -> 42.0
- erofs-utils: update 1.3 -> 1.4
- ethtool: update to 5.16
- expat: upgrade 2.4.1 -> 2.4.7
- ffmpeg: update 4.4 -> 5.0
- file: upgrade 5.40 -> 5.41
- findutils: upgrade 4.8.0 -> 4.9.0
- flac: upgrade 1.3.3 -> 1.3.4
- freetype: upgrade 2.11.0 -> 2.11.1
- fribidi: upgrade 1.0.10 -> 1.0.11
- gawk: update 5.1.0 -> 5.1.1
- gcompat: Update to latest
- gdbm: upgrade 1.19 -> 1.23
- gdb: Upgrade to 11.2
- ghostscript: update 9.54.0 -> 9.55.0
- gi-docgen: upgrade 2021.7 -> 2022.1
- git: update 2.33.0 -> 2.35.2
- glib-2.0: update 2.68.4 -> 2.72.0
- glibc: Upgrade to 2.35
- glib-networking: update 2.68.2 -> 2.72.0
- glslang: update 11.5.0 -> 11.8.0
- gnu-config: update to latest revision
- gnupg: update 2.3.1 -> 2.3.4
- gnutls: update 3.7.2 -> 3.7.4
- gobject-introspection: upgrade 1.68.0 -> 1.72.0
- go-helloworld: update to latest revision
- go: update 1.16.7 -> 1.17.8
- gpgme: upgrade 1.16.0 -> 1.17.1
- gsettings-desktop-schemas: upgrade 40.0 -> 42.0
- gst-devtools: 1.18.4 -> 1.20.1
- gst-examples: 1.18.4 -> 1.18.6
- gstreamer1.0: 1.18.4 -> 1.20.1
- gstreamer1.0-libav: 1.18.4 -> 1.20.1
- gstreamer1.0-omx: 1.18.4 -> 1.20.1
- gstreamer1.0-plugins-bad: 1.18.4  1.20.1
- gstreamer1.0-plugins-base: 1.18.4 -> 1.20.1
- gstreamer1.0-plugins-good: 1.18.4 -> 1.20.1
- gstreamer1.0-plugins-ugly: 1.18.4 -> 1.20.1
- gstreamer1.0-python: 1.18.4 -> 1.20.1
- gstreamer1.0-rtsp-server: 1.18.4 -> 1.20.1
- gstreamer1.0-vaapi: 1.18.4 -> 1.20.1
- gtk+3: upgrade 3.24.30 -> 3.24.33
- gzip: upgrade 1.10 -> 1.12
- harfbuzz: upgrade 2.9.0 -> 4.0.1
- hdparm: upgrade 9.62 -> 9.63
- help2man: upgrade 1.48.4 -> 1.49.1
- icu: update 69.1 -> 70.1
- ifupdown: upgrade 0.8.36 -> 0.8.37
- inetutils: update 2.1 -> 2.2
- init-system-helpers: upgrade 1.60 -> 1.62
- iproute2: update to 5.17.0
- iputils: update 20210722 to 20211215
- iso-codes: upgrade 4.6.0 -> 4.9.0
- itstool: update 2.0.6 -> 2.0.7
- iw: upgrade 5.9 -> 5.16
- json-glib: upgrade 1.6.4 -> 1.6.6
- kea: update 1.8.2 -> 2.0.2
- kexec-tools: update 2.0.22 -> 2.0.23
- less: upgrade 590 -> 600
- libarchive: upgrade 3.5.1 -> 3.6.1
- libatomic-ops: upgrade 7.6.10 -> 7.6.12
- libbsd: upgrade 0.11.3 -> 0.11.5
- libcap: update 2.51 -> 2.63
- libcgroup: upgrade 2.0 -> 2.0.1
- libcomps: upgrade 0.1.17 -> 0.1.18
- libconvert-asn1-perl: upgrade 0.31 -> 0.33
- libdazzle: upgrade 3.40.0 -> 3.44.0
- libdnf: update 0.63.1 -> 0.66.0
- libdrm: upgrade 2.4.107 -> 2.4.110
- libedit: upgrade 20210714-3.1 -> 20210910-3.1
- liberation-fonts: update 2.1.4 -> 2.1.5
- libevdev: upgrade 1.11.0 -> 1.12.1
- libexif: update 0.6.22 -> 0.6.24
- libgit2: update 1.1.1 -> 1.4.2
- libgpg-error: update 1.42 -> 1.44
- libhandy: update 1.2.3 -> 1.5.0
- libical: upgrade 3.0.10 -> 3.0.14
- libinput: update to 1.19.3
- libjitterentropy: update 3.1.0 -> 3.4.0
- libjpeg-turbo: upgrade 2.1.1 -> 2.1.3
- libmd: upgrade 1.0.3 -> 1.0.4
- libmicrohttpd: upgrade 0.9.73 -> 0.9.75
- libmodulemd: upgrade 2.13.0 -> 2.14.0
- libpam: update 1.5.1 -> 1.5.2
- libpcre2: upgrade 10.37 -> 10.39
- libpipeline: upgrade 1.5.3 -> 1.5.5
- librepo: upgrade 1.14.1 -> 1.14.2
- librsvg: update 2.40.21 -> 2.52.7
- libsamplerate0: update 0.1.9 -> 0.2.2
- libsdl2: update 2.0.16 -> 2.0.20
- libseccomp: update to 2.5.3
- libsecret: upgrade 0.20.4 -> 0.20.5
- libsndfile1: bump to version 1.0.31
- libsolv: upgrade 0.7.19 -> 0.7.22
- libsoup-2.4: upgrade 2.72.0 -> 2.74.2
- libsoup: add a recipe for 3.0.5
- libssh2: update 1.9.0 -> 1.10.0
- libtasn1: upgrade 4.17.0 -> 4.18.0
- libtool: Upgrade 2.4.6 -> 2.4.7
- libucontext: Upgrade to 1.2 release
- libunistring: update 0.9.10 -> 1.0
- libunwind: upgrade 1.5.0 -> 1.6.2
- liburcu: upgrade 0.13.0 -> 0.13.1
- libusb1: upgrade 1.0.24 -> 1.0.25
- libuv: update 1.42.0 -> 1.44.1
- libva: update 2.12.0 -> 2.14.0
- libva-utils: upgrade 2.13.0 -> 2.14.0
- libwebp: 1.2.1 -> 1.2.2
- libwpe: upgrade 1.10.1 -> 1.12.0
- libx11: update to 1.7.3.1
- libxcrypt: upgrade 4.4.26 -> 4.4.27
- libxcrypt-compat: upgrade 4.4.26 -> 4.4.27
- libxi: update to 1.8
- libxkbcommon: update to 1.4.0
- libxml2: update to 2.9.13
- libxslt: update to v1.1.35
- lighttpd: update 1.4.59 -> 1.4.64
- linux-firmware: upgrade 20210818 -> 20220310
- linux-libc-headers: update to v5.16
- linux-yocto/5.10: update to v5.10.109
- linux-yocto/5.15: introduce recipes (v5.15.32)
- linux-yocto-dev: update to v5.18+
- linux-yocto-rt/5.10: update to -rt61
- linux-yocto-rt/5.15: update to -rt34
- llvm: update 12.0.1 -> 13.0.1
- logrotate: update 3.18.1 -> 3.19.0
- lsof: update 4.91 -> 4.94.0
- ltp: update 20210927 -> 20220121
- ltp: Update to 20210927
- lttng-modules: update devupstream to latest 2.13
- lttng-modules: update to 2.13.3
- lttng-tools: upgrade 2.13.0 -> 2.13.4
- lttng-ust: upgrade 2.13.0 -> 2.13.2
- lua: update 5.3.6 -> 5.4.4
- lzip: upgrade 1.22 -> 1.23
- man-db: upgrade 2.9.4 -> 2.10.2
- man-pages: update to 5.13
- mdadm: update 4.1 -> 4.2
- mesa: upgrade 21.2.1 -> 22.0.0
- meson: update 0.58.1 -> 0.61.3
- minicom: Upgrade 2.7.1 -> 2.8
- mmc-utils: upgrade to latest revision
- mobile-broadband-provider-info: upgrade 20210805 -> 20220315
- mpg123: upgrade 1.28.2 -> 1.29.3
- msmtp: upgrade 1.8.15 -> 1.8.20
- mtd-utils: upgrade 2.1.3 -> 2.1.4
- mtools: upgrade 4.0.35 -> 4.0.38
- musl: Update to latest master
- ncurses: update 6.2 -> 6.3
- newlib: Upgrade 4.1.0 -> 4.2.0
- nfs-utils: upgrade 2.5.4 -> 2.6.1
- nghttp2: upgrade 1.45.1 -> 1.47.0
- ofono: upgrade 1.32 -> 1.34
- opensbi: Upgrade to 1.0
- openssh: upgrade 8.7p1 -> 8.9
- openssl: update 1.1.1l -> 3.0.2
- opkg: upgrade 0.4.5 -> 0.5.0
- opkg-utils: upgrade 0.4.5 -> 0.5.0
- ovmf: update 202105 -> 202202
- p11-kit: update 0.24.0 -> 0.24.1
- pango: upgrade 1.48.9 -> 1.50.4
- patchelf: upgrade 0.13 -> 0.14.5
- perl-cross: update 1.3.6 -> 1.3.7
- perl: update 5.34.0 -> 5.34.1
- piglit: upgrade to latest revision
- pigz: upgrade 2.6 -> 2.7
- pinentry: update 1.1.1 -> 1.2.0
- pkgconfig: Update to latest
- psplash: upgrade to latest revision
- puzzles: upgrade to latest revision
- python3-asn1crypto: upgrade 1.4.0 -> 1.5.1
- python3-attrs: upgrade 21.2.0 -> 21.4.0
- python3-cryptography: Upgrade to 36.0.2
- python3-cryptography-vectors: upgrade to 36.0.2
- python3-cython: upgrade 0.29.24 -> 0.29.28
- python3-dbusmock: update to 0.27.3
- python3-docutils: upgrade 0.17.1 0.18.1
- python3-dtschema: upgrade 2021.10 -> 2022.1
- python3-gitdb: upgrade 4.0.7 -> 4.0.9
- python3-git: update to 3.1.27
- python3-hypothesis: upgrade 6.15.0 -> 6.39.5
- python3-imagesize: upgrade 1.2.0 -> 1.3.0
- python3-importlib-metadata: upgrade 4.6.4 -> 4.11.3
- python3-jinja2: upgrade 3.0.1 -> 3.1.1
- python3-jsonschema: upgrade 3.2.0 -> 4.4.0
- python3-libarchive-c: upgrade 3.1 -> 4.0
- python3-magic: upgrade 0.4.24 -> 0.4.25
- python3-mako: upgrade 1.1.5 -> 1.1.6
- python3-markdown: upgrade 3.3.4 -> 3.3.6
- python3-markupsafe: upgrade 2.0.1 -> 2.1.1
- python3-more-itertools: upgrade 8.8.0 -> 8.12.0
- python3-numpy: upgrade 1.21.2 -> 1.22.3
- python3-packaging: upgrade 21.0 -> 21.3
- python3-pathlib2: upgrade 2.3.6 -> 2.3.7
- python3-pbr: upgrade 5.6.0 -> 5.8.1
- python3-pip: update 21.2.4 -> 22.0.3
- python3-pycairo: upgrade 1.20.1 -> 1.21.0
- python3-pycryptodome: upgrade 3.10.1 -> 3.14.1
- python3-pyelftools: upgrade 0.27 -> 0.28
- python3-pygments: upgrade 2.10.0 -> 2.11.2
- python3-pygobject: upgrade 3.40.1 -> 3.42.0
- python3-pyparsing: update to 3.0.7
- python3-pyrsistent: upgrade 0.18.0 -> 0.18.1
- python3-pytest-runner: upgrade 5.3.1 -> 6.0.0
- python3-pytest-subtests: upgrade 0.6.0 -> 0.7.0
- python3-pytest: upgrade 6.2.4 -> 7.1.1
- python3-pytz: upgrade 2021.3 -> 2022.1
- python3-py: upgrade 1.10.0 -> 1.11.0
- python3-pyyaml: upgrade 5.4.1 -> 6.0
- python3-ruamel-yaml: upgrade 0.17.16 -> 0.17.21
- python3-scons: upgrade 4.2.0 -> 4.3.0
- python3-setuptools-scm: upgrade 6.0.1 -> 6.4.2
- python3-setuptools: update to 59.5.0
- python3-smmap: update to 5.0.0
- python3-tomli: upgrade 1.2.1 -> 2.0.1
- python3: update to 3.10.3
- python3-urllib3: upgrade 1.26.8 -> 1.26.9
- python3-zipp: upgrade 3.5.0 -> 3.7.0
- qemu: update 6.0.0 -> 6.2.0
- quilt: upgrade 0.66 -> 0.67
- re2c: upgrade 2.2 -> 3.0
- readline: upgrade 8.1 -> 8.1.2
- repo: upgrade 2.17.3 -> 2.22
- resolvconf: update 1.87 -> 1.91
- rng-tools: upgrade 6.14 -> 6.15
- rpcsvc-proto: upgrade 1.4.2 -> 1.4.3
- rpm: update 4.16.1.3 -> 4.17.0
- rt-tests: update 2.1 -> 2.3
- ruby: update 3.0.2 -> 3.1.1
- rust: update 1.54.0 -> 1.59.0
- rxvt-unicode: upgrade 9.26 -> 9.30
- screen: upgrade 4.8.0 -> 4.9.0
- shaderc: update 2021.1 -> 2022.1
- shadow: upgrade 4.9 -> 4.11.1
- socat: upgrade 1.7.4.1 -> 1.7.4.3
- spirv-headers: bump to b42ba6 revision
- spirv-tools: update 2021.2 -> 2022.1
- sqlite3: upgrade 3.36.0 -> 3.38.2
- strace: update 5.14 -> 5.16
- stress-ng: upgrade 0.13.00 -> 0.13.12
- sudo: update 1.9.7p2 -> 1.9.10
- sysklogd: upgrade 2.2.3 -> 2.3.0
- sysstat: upgrade 12.4.3 -> 12.4.5
- systemd: update 249.3 -> 250.4
- systemtap: upgrade 4.5 -> 4.6
- sysvinit: upgrade 2.99 -> 3.01
- tzdata: update to 2022a
- u-boot: upgrade 2021.07 -> 2022.01
- uninative: Upgrade to 3.6 with gcc 12 support
- util-linux: update 2.37.2 -> 2.37.4
- vala: upgrade 0.52.5 -> 0.56.0
- valgrind: update 3.17.0 -> 3.18.1
- vim: upgrade to 8.2 patch 4681
- vte: upgrade 0.64.2 -> 0.66.2
- vulkan-headers: upgrade 1.2.182 -> 1.2.191
- vulkan-loader: upgrade 1.2.182 -> 1.2.198.1
- vulkan-samples: update to latest revision
- vulkan-tools: upgrade 1.2.182 -> 1.2.191
- vulkan: update 1.2.191.0 -> 1.3.204.1
- waffle: update 1.6.1 -> 1.7.0
- wayland-protocols: upgrade 1.21 -> 1.25
- wayland: upgrade 1.19.0 -> 1.20.0
- webkitgtk: upgrade 2.34.0 -> 2.36.0
- weston: upgrade 9.0.0 -> 10.0.0
- wget: update 1.21.1 -> 1.21.3
- wireless-regdb: upgrade 2021.07.14 -> 2022.02.18
- wpa-supplicant: update 2.9 -> 2.10
- wpebackend-fdo: upgrade 1.10.0 -> 1.12.0
- xauth: upgrade 1.1 -> 1.1.1
- xf86-input-libinput: update to 1.2.1
- xf86-video-intel: update to latest commit
- xkeyboard-config: update to 2.35.1
- xorgproto: update to 2021.5
- xserver-xorg: update 1.20.13 -> 21.1.3
- xwayland: update 21.1.2 -> 22.1.0
- xxhash: upgrade 0.8.0 -> 0.8.1
- zstd: update 1.5.0 -> 1.5.2



Contributors to 4.0
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

- Abongwa Amahnui Bonalais
- Adriaan Schmidt
- Adrian Freihofer
- Ahmad Fatoum
- Ahmed Hossam
- Ahsan Hussain
- Alejandro Hernandez Samaniego
- Alessio Igor Bogani
- Alexander Kanavin
- Alexandre Belloni
- Alexandru Ardelean
- Alexey Brodkin
- Alex Stewart
- Andreas Müller
- Andrei Gherzan
- Andrej Valek
- Andres Beltran
- Andrew Jeffery
- Andrey Zhizhikin
- Anton Mikanovich
- Anuj Mittal
- Bill Pittman
- Bruce Ashfield
- Caner Altinbasak
- Carlos Rafael Giani
- Chaitanya Vadrevu
- Changhyeok Bae
- Changqing Li
- Chen Qi
- Christian Eggers
- Claudius Heine
- Claus Stovgaard
- Daiane Angolini
- Daniel Ammann
- Daniel Gomez
- Daniel McGregor
- Daniel Müller
- Daniel Wagenknecht
- David Joyner
- David Reyna
- Denys Dmytriyenko
- Dhruva Gole
- Diego Sueiro
- Dmitry Baryshkov
- Ferry Toth
- Florian Amstutz
- Henry Kleynhans
- He Zhe
- Hongxu Jia
- Hsia-Jun(Randy) Li
- Ian Ray
- Jacob Kroon
- Jagadeesh Krishnanjanappa
- Jasper Orschulko
- Jim Wilson
- Joel Winarske
- Joe Slater
- Jon Mason
- Jose Quaresma
- Joshua Watt
- Justin Bronder
- Kai Kang
- Kamil Dziezyk
- Kevin Hao
- Khairul Rohaizzat Jamaluddin
- Khem Raj
- Kiran Surendran
- Konrad Weihmann
- Kory Maincent
- Lee Chee Yang
- Leif Middelschulte
- Lei Maohui
- Li Wang
- Liwei Song
- Luca Boccassi
- Lukasz Majewski
- Luna Gräfje
- Manuel Leonhardt
- Marek Vasut
- Mark Hatle
- Markus Niebel
- Markus Volk
- Marta Rybczynska
- Martin Beeger
- Martin Jansa
- Matthias Klein
- Matt Madison
- Maximilian Blenk
- Max Krummenacher
- Michael Halstead
- Michael Olbrich
- Michael Opdenacker
- Mike Crowe
- Ming Liu
- Mingli Yu
- Minjae Kim
- Nicholas Sielicki
- Olaf Mandel
- Oleh Matiusha
- Oleksandr Kravchuk
- Oleksandr Ocheretnyi
- Oleksandr Suvorov
- Oleksiy Obitotskyy
- Otavio Salvador
- Pablo Saavedra
- Paul Barker
- Paul Eggleton
- Pavel Zhukov
- Peter Hoyes
- Peter Kjellerstedt
- Petr Vorel
- Pgowda
- Quentin Schulz
- Ralph Siemsen
- Randy Li
- Randy MacLeod
- Rasmus Villemoes
- Ricardo Salveti
- Richard Neill
- Richard Purdie
- Robert Joslyn
- Robert P. J. Day
- Robert Yang
- Ross Burton
- Rudolf J Streif
- Sakib Sajal
- Samuli Piippo
- Saul Wold
- Scott Murray
- Sean Anderson
- Simone Weiss
- Simon Kuhnle
- S. Lockwood-Childs
- Stefan Herbrechtsmeier
- Steve Sakoman
- Sundeep KOKKONDA
- Tamizharasan Kumar
- Tean Cunningham
- Teoh Jay Shen
- Thomas Perrot
- Tim Orling
- Tobias Kaufmann
- Tom Hochstein
- Tony McDowell
- Trevor Gamblin
- Ulrich Ölmann
- Valerii Chernous
- Vivien Didelot
- Vyacheslav Yurkov
- Wang Mingyu
- Xavier Berger
- Yi Zhao
- Yongxin Liu
- Yureka
- Zev Weiss
- Zheng Ruoqin
- Zoltán Böszörményi
- Zygmunt Krynicki



Repositories / Downloads for 4.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0 </poky/tag/?h=yocto-4.0>`
-  Git Revision: :yocto_git:`00cfdde791a0176c134f31e5a09eff725e75b905 </poky/commit/?id=00cfdde791a0176c134f31e5a09eff725e75b905>`
-  Release Artefact: poky-00cfdde791a0176c134f31e5a09eff725e75b905
-  sha: 4cedb491b7bf0d015768c61690f30d7d73f4266252d6fba907bba97eac83648c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0/poky-00cfdde791a0176c134f31e5a09eff725e75b905.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0/poky-00cfdde791a0176c134f31e5a09eff725e75b905.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag: :oe_git:`yocto-4.0 </openembedded-core/tag/?h=yocto-4.0>`
-  Git Revision: :oe_git:`92fcb6570bddd0c5717d8cfdf38ecf3e44942b0f </openembedded-core/commit/?id=92fcb6570bddd0c5717d8cfdf38ecf3e44942b0f>`
-  Release Artefact: oecore-92fcb6570bddd0c5717d8cfdf38ecf3e44942b0f
-  sha: c042629752543a10b0384b2076b1ee8742fa5e8112aef7b00b3621f8387a51c6
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0/oecore-92fcb6570bddd0c5717d8cfdf38ecf3e44942b0f.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0/oecore-92fcb6570bddd0c5717d8cfdf38ecf3e44942b0f.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0 </meta-mingw/tag/?h=yocto-4.0>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0 </meta-gplv2/tag/?h=yocto-4.0>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-mingw/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag: :oe_git:`yocto-4.0 </bitbake/tag/?h=yocto-4.0>`
-  Git Revision: :oe_git:`c212b0f3b542efa19f15782421196b7f4b64b0b9 </bitbake/commit/?id=c212b0f3b542efa19f15782421196b7f4b64b0b9>`
-  Release Artefact: bitbake-c212b0f3b542efa19f15782421196b7f4b64b0b9
-  sha: 6872095c7d7be5d791ef3e18b6bab2d1e0e237962f003d2b00dc7bd6fb6d2ef7
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0/bitbake-c212b0f3b542efa19f15782421196b7f4b64b0b9.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0/bitbake-c212b0f3b542efa19f15782421196b7f4b64b0b9.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0 </yocto-docs/tag/?h=yocto-4.0>`
-  Git Revision: :yocto_git:`a6f571ad5b087385cad8765ed455c4b4eaeebca6 </yocto-docs/commit/?id=a6f571ad5b087385cad8765ed455c4b4eaeebca6>`

