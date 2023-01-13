.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 3.4 (honister)
--------------------------------

New Features / Enhancements in 3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 5.14, glibc 2.34 and ~280 other recipe upgrades
-  Switched override character to ':' (replacing '_') for more robust parsing and improved performance --- see the above migration guide for help
-  Rust integrated into core, providing rust support for cross-compilation and SDK
-  New :ref:`ref-classes-create-spdx` class for creating SPDX SBoM documents
-  New recipes: cargo, core-image-ptest-all, core-image-ptest-fast, core-image-weston-sdk, erofs-utils, gcompat, gi-docgen, libmicrohttpd, libseccomp, libstd-rs, perlcross, python3-markdown, python3-pyyaml, python3-smartypants, python3-typogrify, rust, rust-cross, rust-cross-canadian, rust-hello-world, rust-llvm, rust-tools-cross-canadian, rustfmt, xwayland
-  Several optimisations to reduce unnecessary task dependencies for faster builds
-  seccomp integrated into core, with additional enabling for gnutls, systemd, qemu
-  New overlayfs class to help generate overlayfs mount units
-  debuginfod support now enabled by default
-  Switched several recipes over to using OpenSSL instead of GnuTLS (wpa-supplicant, curl, glib-networking) or disable GnuTLS (cups) by default
-  Improvements to LTO plugin installation and reproducibility
-  Architecture-specific enhancements:

   -  glibc: Enable memory tagging for aarch64
   -  testimage: remove aarch64 xorg exclusion
   -  arch-arm*: add better support for gcc march extensions
   -  tune-cortexm*: add support for all Arm Cortex-M processors
   -  tune-cortexr*: add support for all Arm Cortex-R processors
   -  arch-armv4: Allow -march=armv4
   -  qemuarm*: use virtio graphics
   -  baremetal-helloworld: Enable RISC-V 64/32 port
   -  ldconfig-native: Add RISC-V support
   -  qemuriscv: Enable 4 core emulation
   -  Add ARC support in gdb, dpkg, dhcpcd
   -  conf/machine-sdk: Add ppc64 SDK machine
   -  libjpeg-turbo: Handle powerpc64le without Altivec
   -  pixman: Handle PowerPC without Altivec
   -  mesa: enable gallium Intel drivers when building for x86
   -  mesa: enable crocus driver for older Intel graphics

-  Kernel-related enhancements:

   -  Support zstd-compressed modules and :term:`Initramfs` images
   -  Allow opt-out of split kernel modules
   -  linux-yocto-dev: base :term:`AUTOREV` on specified version
   -  kernel-yocto: provide debug / summary information for metadata
   -  kernel-uboot: Handle gzip and lzo compression options
   -  linux-yocto/5.14: added devupstream support
   -  linux-yocto: add vfat to :term:`KERNEL_FEATURES` when :term:`MACHINE_FEATURES` include vfat
   -  linux-yocto: enable TYPEC_TCPCI in usbc fragment

-  Image-related enhancements:

   -  New erofs, erofs-lz4 and erofs-lz4hc image types
   -  New squashfs-zst and cpio.zst image types
   -  New lic-pkgs :term:`IMAGE_FEATURES` item to install all license packages
   -  Added zsync metadata conversion support
   -  Use xargs to set file timestamps for significant (>90%) do_image speedup
   -  Find .ko.gz and .ko.xz kernel modules as well when determining need to run depmod on an image
   -  Show formatted error messages instead of tracebacks for systemctl errors
   -  No longer ignore installation failures in complementary package installation
   -  Remove ldconfig auxiliary cache when not needed

-  wic enhancements:

   -  Added erofs filesystem support
   -  Added ``--extra-space argument`` to leave extra space after last partition
   -  Added ``--no-fstab-update`` part option to allow using the stock fstab
   -  bootimg-efi: added Unified Kernel Image option
   -  bootimg-pcbios: use label provided when formatting a DOS partition

-  SDK-related enhancements:

   -  Enable :ref:`ref-tasks-populate_sdk` with multilibs
   -  New ``SDKPATHINSTALL`` variable decouples default install path from
      built in path to avoid rebuilding :ref:`ref-classes-nativesdk`
      components on e.g. :term:`DISTRO_VERSION` changes
   -  eSDK: Error if trying to generate an eSDK from a multiconfig
   -  eSDK: introduce :term:`TOOLCHAIN_HOST_TASK_ESDK` to be used in place of :term:`TOOLCHAIN_HOST_TASK` to add components to the host part of the eSDK

-  BitBake enhancements:

   -  New bitbake-getvar helper command to query a variable value (with history)
   -  bitbake-layers: layerindex-fetch: add ``--fetchdir`` parameter
   -  bitbake-layers: show-recipes: add skip reason to output
   -  bitbake-diffsigs: sort diff output for consistency
   -  Allow setting upstream for local hash equivalence server
   -  fetch2/s3: allow to use credentials and switch profile from environment variables
   -  fetch2/s3: Add progress handler for S3 cp command
   -  fetch2/npm: Support npm archives with missing search directory mode
   -  fetch2/npmsw: Add support for local tarball and link sources
   -  fetch2/svn: Allow peg-revision functionality to be disabled
   -  fetch2/wget: verify certificates for HTTPS/FTPS by default
   -  fetch2/wget: Enable FTPS
   -  prserv: added read-only mode
   -  prserv: replaced XML RPC with modern asyncrpc implementation
   -  Numerous warning/error message improvements

-  New :term:`PACKAGECONFIG` options in btrfs-tools, ccache, coreutils, cups, dbus, elfutils, ffmpeg, findutils, glib-2.0, gstreamer1.0-plugins-bad, gstreamer1.0-plugins-base, libarchive, libnotify, libpsl, man-db, mesa, ovmf, parted, prelink, qemu, rpm, shadow, systemd, tar, vim, weston
-  u-boot enhancements:

   -  Make SPL suffix configurable
   -  Make ``UBOOT_BINARYNAME`` configurable
   -  Package ``extlinux.conf`` separately
   -  Allow deploying the u-boot DTB

-  opensbi: Add support for specifying a device tree
-  busybox enhancements:

   -  Added tmpdir option into mktemp applet
   -  Support mounting swap via labels
   -  Enable long options for enabled applets

-  Move tune files to architecture subdirectories
-  buildstats: log host data on failure separately to task specific file
-  buildstats: collect "at interval" and "on failure" logs in the same file
-  Ptest enhancements:

   -  ptest-runner: install script to collect system data on failure
   -  Added ptest support to python3-hypothesis, python3-jinja2, python3-markupsafe
   -  Enhanced ptest support in lttng, util-linux, and others
   -  New leaner ptest image recipes based upon core-image-minimal

-  scripts/contrib/image-manifest: add new script
-  Add beginnings of Android target support
-  devtool upgrade: rebase override-only patches as well
-  devtool: print a warning on upgrades if :term:`PREFERRED_VERSION` is set
-  systemd: set zstd as default compression option
-  init-manager-systemd: add a weak VIRTUAL-RUNTIME_dev_manager assignment
-  Add proper unpack dependency for .zst compressed archives
-  util-linux: build chfn and chsh by default
-  qemu: use 4 cores in qemu guests
-  runqemu: decouple bios and kernel options
-  qemu: add a hint on how to enable CPU render nodes when a suitable GPU is absent
-  devupstream: Allow support of native class extensions
-  Prelinking now disabled in default configuration
-  python3: statistics module moved to its own python3-statistics package
-  pypi: allow override of PyPI archive name
-  Allow global override of golang GO_DYNLINK
-  buildhistory enhancements:

   -  Add option to strip path prefix
   -  Add output file listing package information
   -  Label packages providing per-file dependencies in depends.dot

-  New gi-docgen class for GNOME library documentation
-  meson.bbclass: Make the default buildtype "debug" if :term:`DEBUG_BUILD` is 1
-  distro_features_check: expand with :term:`IMAGE_FEATURES`
-  Add extended packagedata in JSON format
-  local.conf.sample: Update sstate mirror entry with new hash equivalence setting
-  poky: Use https in default :term:`PREMIRRORS`
-  reproducible_build.bbclass: Enable -Wdate-time
-  yocto-check-layer: ensure that all layer dependencies are tested too
-  core-image-multilib-example: base on weston, and not sato
-  npm.bbclass: Allow nodedir to be overridden by ``NPM_NODEDIR``
-  cve-extra-exclusions.inc: add exclusion list for intractable CVE's
-  license_image.bbclass: Detect broken symlinks
-  sysstat: make the service start automatically
-  sanity: Add error check for '%' in build path
-  sanity: Further improve directory sanity tests
-  sanity.bbclass: mention ``CONNECTIVITY_CHECK_URIS`` in network failure message
-  tzdata: Allow controlling zoneinfo binary format
-  oe-time-dd-test.sh: add options and refactor
-  vim: add option to disable NLS support
-  zstd: Include pzstd in the build
-  mirrors.bbclass: provide additional rule for git repo fallbacks
-  own-mirrors: Add support for s3:// scheme in :term:`SOURCE_MIRROR_URL`
-  common-licenses: add missing SPDX licences
-  Add MAINTAINERS.md file to record subsystem maintainers

Known Issues in 3.4
~~~~~~~~~~~~~~~~~~~

- Build failures have been reported when running on host Linux systems with FIPS enabled (such as RHEL 8.0 with the FIPS mode enabled). For more details please see :yocto_bugs:`bug #14609 </show_bug.cgi?id=14609>`.

Recipe Licenses changes in 3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

-  acpica: correct :term:`LICENSE` to "Intel | BSD-3-Clause | GPLv2"
-  dtc: correct :term:`LICENSE` to "GPLv2 | BSD-2-Clause"
-  e2fsprogs: correct :term:`LICENSE` to "GPLv2 & LGPLv2 & BSD-3-Clause & MIT"
-  ffmpeg: correct :term:`LICENSE` to "GPLv2+ & LGPLv2.1+ & ISC & MIT & BSD-2-Clause & BSD-3-Clause & IJG"
-  flac: correct :term:`LICENSE` to "GFDL-1.2 & GPLv2+ & LGPLv2.1+ & BSD-3-Clause"
-  flex: correct :term:`LICENSE` to "BSD-3-Clause & LGPL-2.0+"
-  font-util: correct :term:`LICENSE` to "MIT & MIT-style & BSD-4-Clause & BSD-2-Clause"
-  glib-2.0: correct :term:`LICENSE` to "LGPLv2.1+ & BSD-3-Clause & PD"
-  gobject-introspection: correct :term:`LICENSE` to "LGPLv2+ & GPLv2+ & MIT" (add MIT license)
-  hdparm: correct :term:`LICENSE` to "BSD-2-Clause & GPLv2 & hdparm"
-  iputils: correct :term:`LICENSE` to "BSD-3-Clause & GPLv2+"
-  libcap: correct :term:`LICENSE` to "BSD-3-Clause | GPLv2"
-  libevent: correct :term:`LICENSE` to "BSD-3-Clause & MIT"
-  libjitterentropy: correct :term:`LICENSE` to "GPLv2+ | BSD-3-Clause"
-  libpam: correct :term:`LICENSE` to "GPLv2+ | BSD-3-Clause"
-  libwpe: correct :term:`LICENSE` to "BSD-2-Clause"
-  libx11-compose-data: correct :term:`LICENSE` to "MIT & MIT-style & BSD-4-Clause & BSD-2-Clause"
-  libx11: correct :term:`LICENSE` to "MIT & MIT-style & BSD-4-Clause & BSD-2-Clause"
-  libxfont2: correct :term:`LICENSE` to "MIT & MIT-style & BSD-4-Clause & BSD-2-Clause"
-  libxfont: correct :term:`LICENSE` to "MIT & MIT-style & BSD-3-Clause"
-  lsof: correct :term:`LICENSE` to reflect that it uses a BSD-like (but not exactly BSD) license ("Spencer-94")
-  nfs-utils: correct :term:`LICENSE` to "MIT & GPLv2+ & BSD-3-Clause"
-  ovmf: correct license to "BSD-2-Clause-Patent"
-  ppp: correct :term:`LICENSE` to "BSD-3-Clause & BSD-3-Clause-Attribution & GPLv2+ & LGPLv2+ & PD"
-  python3-packaging: correct :term:`LICENSE` to "Apache-2.0 | BSD-2-Clause"
-  python-async-test: correct :term:`LICENSE` to "BSD-3-Clause"
-  quota: remove BSD license (only BSD licensed part of the code was removed in 4.05)
-  shadow: correct :term:`LICENSE` to "BSD-3-Clause | Artistic-1.0"
-  shadow-sysroot: set :term:`LICENSE` the same as shadow
-  sudo: correct :term:`LICENSE` to "ISC & BSD-3-Clause & BSD-2-Clause & Zlib"
-  swig: correct :term:`LICENSE` to "BSD-3-Clause & GPLv3"
-  valgrind: correct license to "GPLv2 & GPLv2+ & BSD-3-Clause"
-  webkitgtk: correct :term:`LICENSE` to "BSD-2-Clause & LGPLv2+"
-  wpebackend-fdo: correct :term:`LICENSE` to "BSD-2-Clause"
-  xinetd: correct :term:`LICENSE` to reflect that it uses a unique BSD-like (but not exactly BSD) license

Other license-related notes:

-  When creating recipes for Python software, recipetool will now treat "BSD" as "BSD-3-Clause" for the purposes of setting :term:`LICENSE`, as that is the most common understanding.
-  Please be aware that an :term:`Initramfs` bundled with the kernel using :term:`INITRAMFS_IMAGE_BUNDLE` should only contain GPLv2-compatible software; this is now mentioned in the documentation.

Security Fixes in 3.4
~~~~~~~~~~~~~~~~~~~~~

-  apr: :cve:`2021-35940`
-  aspell: :cve:`2019-25051`
-  avahi: :cve:`2021-3468`, :cve:`2021-36217`
-  binutils: :cve:`2021-20197`
-  bluez: :cve:`2021-3658`
-  busybox: :cve:`2021-28831`
-  cairo: :cve:`2020-35492`
-  cpio: :cve:`2021-38185`
-  expat: :cve:`2013-0340`
-  ffmpeg: :cve:`2020-20446`, :cve:`2020-22015`, :cve:`2020-22021`, :cve:`2020-22033`, :cve:`2020-22019`, :cve:`2021-33815`, :cve:`2021-38171`, :cve:`2020-20453`
-  glibc: :cve:`2021-33574`, :cve:`2021-38604`
-  inetutils: :cve:`2021-40491`
-  libgcrypt: :cve:`2021-40528`
-  linux-yocto/5.10, 5.14: :cve:`2021-3653`, :cve:`2021-3656`
-  lz4: :cve:`2021-3520`
-  nettle: :cve:`2021-20305`
-  openssl: :cve:`2021-3711`, :cve:`2021-3712`
-  perl: :cve:`2021-36770`
-  python3: :cve:`2021-29921`
-  python3-pip: :cve:`2021-3572`
-  qemu: :cve:`2020-27821`, :cve:`2020-29443`, :cve:`2020-35517`, :cve:`2021-3392`, :cve:`2021-3409`, :cve:`2021-3416`, :cve:`2021-3527`, :cve:`2021-3544`, :cve:`2021-3545`, :cve:`2021-3546`, :cve:`2021-3682`, :cve:`2021-20181`, :cve:`2021-20221`, :cve:`2021-20257`, :cve:`2021-20263`
-  rpm: :cve:`2021-3421`, :cve:`2021-20271`
-  rsync: :cve:`2020-14387`
-  util-linux: :cve:`2021-37600`
-  vim: :cve:`2021-3770`, :cve:`2021-3778`
-  wpa-supplicant: :cve:`2021-30004`
-  xdg-utils: :cve:`2020-27748`
-  xserver-xorg: :cve:`2021-3472`

Recipe Upgrades in 3.4
~~~~~~~~~~~~~~~~~~~~~~

-  acl 2.2.53 -> 2.3.1
-  acpica 20210105 -> 20210730
-  alsa-lib 1.2.4 -> 1.2.5.1
-  alsa-plugins 1.2.2 -> 1.2.5
-  alsa-tools 1.2.2 -> 1.2.5
-  alsa-topology-conf 1.2.4 -> 1.2.5.1
-  alsa-ucm-conf 1.2.4 -> 1.2.5.1
-  alsa-utils 1.2.4 -> 1.2.5.1
-  alsa-utils-scripts 1.2.4 -> 1.2.5.1
-  apt 2.2.2 -> 2.2.4
-  at 3.2.1 -> 3.2.2
-  at-spi2-core 2.38.0 -> 2.40.3
-  autoconf-archive 2019.01.06 -> 2021.02.19
-  babeltrace2 2.0.3 -> 2.0.4
-  bash 5.1 -> 5.1.8
-  bind 9.16.16 -> 9.16.20
-  binutils 2.36.1 -> 2.37
-  binutils-cross 2.36.1 -> 2.37
-  binutils-cross-canadian 2.36.1 -> 2.37
-  binutils-cross-testsuite 2.36.1 -> 2.37
-  binutils-crosssdk 2.36.1 -> 2.37
-  bison 3.7.5 -> 3.7.6
-  blktrace 1.2.0+gitX -> 1.3.0+gitX
-  bluez5 5.56 -> 5.61
-  boost 1.75.0 -> 1.77.0
-  boost-build-native 4.3.0 -> 4.4.1
-  btrfs-tools 5.10.1 -> 5.13.1
-  busybox 1.33.1 -> 1.34.0
-  busybox-inittab 1.33.0 -> 1.34.0
-  ccache 4.2 -> 4.4
-  cmake 3.19.5 -> 3.21.1
-  cmake-native 3.19.5 -> 3.21.1
-  connman 1.39 -> 1.40
-  createrepo-c 0.17.0 -> 0.17.4
-  cronie 1.5.5 -> 1.5.7
-  cross-localedef-native 2.33 -> 2.34
-  cups 2.3.3 -> 2.3.3op2
-  curl 7.75.0 -> 7.78.0
-  dbus-glib 0.110 -> 0.112
-  dejagnu 1.6.2 -> 1.6.3
-  diffoscope 172 -> 181
-  diffutils 3.7 -> 3.8
-  distcc 3.3.5 -> 3.4
-  dnf 4.6.0 -> 4.8.0
-  dpkg 1.20.7.1 -> 1.20.9
-  dtc 1.6.0 -> 1.6.1
-  e2fsprogs 1.46.1 -> 1.46.4
-  elfutils 0.183 -> 0.185
-  ell 0.38 -> 0.43
-  enchant2 2.2.15 -> 2.3.1
-  epiphany 3.38.2 -> 40.3
-  ethtool 5.10 -> 5.13
-  expat 2.2.10 -> 2.4.1
-  ffmpeg 4.3.2 -> 4.4
-  file 5.39 -> 5.40
-  freetype 2.10.4 -> 2.11.0
-  gcc 10.2.0 -> 11.2.0
-  gcc-cross 10.2.0 -> 11.2.0
-  gcc-cross-canadian 10.2.0 -> 11.2.0
-  gcc-crosssdk 10.2.0 -> 11.2.0
-  gcc-runtime 10.2.0 -> 11.2.0
-  gcc-sanitizers 10.2.0 -> 11.2.0
-  gcc-source 10.2.0 -> 11.2.0
-  gcr 3.38.1 -> 3.40.0
-  gdb 10.1 -> 10.2
-  gdb-cross 10.1 -> 10.2
-  gdb-cross-canadian 10.1 -> 10.2
-  gdk-pixbuf 2.40.0 -> 2.42.6
-  ghostscript 9.53.3 -> 9.54.0
-  git 2.31.1 -> 2.33.0
-  glib-2.0 2.66.7 -> 2.68.4
-  glib-networking 2.66.0 -> 2.68.2
-  glibc 2.33 -> 2.34
-  glibc-locale 2.33 -> 2.34
-  glibc-mtrace 2.33 -> 2.34
-  glibc-scripts 2.33 -> 2.34
-  glibc-testsuite 2.33 -> 2.34
-  glslang 11.2.0 -> 11.5.0
-  gnome-desktop-testing 2018.1 -> 2021.1
-  gnu-config 20210125+gitX -> 20210722+gitX
-  gnu-efi 3.0.12 -> 3.0.14
-  gnupg 2.2.27 -> 2.3.1
-  gobject-introspection 1.66.1 -> 1.68.0
-  gpgme 1.15.1 -> 1.16.0
-  gptfdisk 1.0.7 -> 1.0.8
-  grep 3.6 -> 3.7
-  grub 2.04+2.06~rc1 -> 2.06
-  grub-efi 2.04+2.06~rc1 -> 2.06
-  gsettings-desktop-schemas 3.38.0 -> 40.0
-  gtk+3 3.24.25 -> 3.24.30
-  harfbuzz 2.7.4 -> 2.9.0
-  hdparm 9.60 -> 9.62
-  help2man 1.48.2 -> 1.48.4
-  hwlatdetect 1.10 -> 2.1
-  i2c-tools 4.2 -> 4.3
-  icu 68.2 -> 69.1
-  igt-gpu-tools 1.25+gitX -> 1.26
-  inetutils 2.0 -> 2.1
-  iproute2 5.11.0 -> 5.13.0
-  iputils s20200821 -> 20210722
-  json-glib 1.6.2 -> 1.6.4
-  kexec-tools 2.0.21 -> 2.0.22
-  kmod 28 -> 29
-  kmod-native 28 -> 29
-  less 563 -> 590
-  libassuan 2.5.4 -> 2.5.5
-  libcap 2.48 -> 2.51
-  libcgroup 0.41 -> 2.0
-  libcomps 0.1.15 -> 0.1.17
-  libconvert-asn1-perl 0.27 -> 0.31
-  libdazzle 3.38.0 -> 3.40.0
-  libdnf 0.58.0 -> 0.63.1
-  libdrm 2.4.104 -> 2.4.107
-  libedit 20210216-3.1 -> 20210714-3.1
-  libepoxy 1.5.5 -> 1.5.9
-  liberation-fonts 2.00.1 -> 2.1.4
-  libffi 3.3 -> 3.4.2
-  libfm 1.3.1 -> 1.3.2
-  libgcc 10.2.0 -> 11.2.0
-  libgcc-initial 10.2.0 -> 11.2.0
-  libgcrypt 1.9.3 -> 1.9.4
-  libgfortran 10.2.0 -> 11.2.0
-  libgit2 1.1.0 -> 1.1.1
-  libglu 9.0.1 -> 9.0.2
-  libgpg-error 1.41 -> 1.42
-  libgudev 234 -> 237
-  libhandy 1.2.0 -> 1.2.3
-  libical 3.0.9 -> 3.0.10
-  libidn2 2.3.0 -> 2.3.2
-  libinput 1.16.4 -> 1.18.1
-  libjitterentropy 3.0.1 -> 3.1.0
-  libjpeg-turbo 2.0.6 -> 2.1.1
-  libksba 1.5.0 -> 1.6.0
-  libmodulemd 2.12.0 -> 2.13.0
-  libnsl2 1.3.0 -> 2.0.0
-  libnss-mdns 0.14.1 -> 0.15.1
-  libogg 1.3.4 -> 1.3.5
-  libpcap 1.10.0 -> 1.10.1
-  libpcre 8.44 -> 8.45
-  libpcre2 10.36 -> 10.37
-  libportal 0.3 -> 0.4
-  librepo 1.13.0 -> 1.14.1
-  libsdl2 2.0.14 -> 2.0.16
-  libsolv 0.7.17 -> 0.7.19
-  libtasn1 4.16.0 -> 4.17.0
-  libtest-needs-perl 0.002006 -> 0.002009
-  libtirpc 1.3.1 -> 1.3.2
-  libubootenv 0.3.1 -> 0.3.2
-  libucontext 0.10+X -> 1.1+X
-  liburcu 0.12.2 -> 0.13.0
-  libuv 1.41.0 -> 1.42.0
-  libva 2.10.0 -> 2.12.0
-  libva-initial 2.10.0 -> 2.12.0
-  libva-utils 2.10.0 -> 2.12.0
-  libwebp 1.2.0 -> 1.2.1
-  libwpe 1.8.0 -> 1.10.1
-  libx11 1.7.0 -> 1.7.2
-  libxcrypt 4.4.18 -> 4.4.25
-  libxcrypt-compat 4.4.18 -> 4.4.25
-  libxfixes 5.0.3 -> 6.0.0
-  libxfont2 2.0.4 -> 2.0.5
-  libxft 2.3.3 -> 2.3.4
-  libxi 1.7.10 -> 1.7.99.2
-  libxkbcommon 1.0.3 -> 1.3.0
-  libxml2 2.9.10 -> 2.9.12
-  libxres 1.2.0 -> 1.2.1
-  linux-libc-headers 5.10 -> 5.14
-  linux-yocto 5.4.144+gitX, 5.10.63+gitX -> 5.10.70+gitX, 5.14.9+gitX
-  linux-yocto-dev 5.12++gitX -> 5.15++gitX
-  linux-yocto-rt 5.4.144+gitX, 5.10.63+gitX -> 5.10.70+gitX, 5.14.9+gitX
-  linux-yocto-tiny 5.4.144+gitX, 5.10.63+gitX -> 5.10.70+gitX, 5.14.9+gitX
-  llvm 11.1.0 -> 12.0.1
-  log4cplus 2.0.6 -> 2.0.7
-  logrotate 3.18.0 -> 3.18.1
-  ltp 20210121 -> 20210524
-  lttng-modules 2.12.6 -> 2.13.0
-  lttng-tools 2.12.4 -> 2.13.0
-  lttng-ust 2.12.1 -> 2.13.0
-  m4 1.4.18 -> 1.4.19
-  m4-native 1.4.18 -> 1.4.19
-  man-pages 5.10 -> 5.12
-  mc 4.8.26 -> 4.8.27
-  mesa 21.0.3 -> 21.2.1
-  mesa-gl 21.0.3 -> 21.2.1
-  meson 0.57.1 -> 0.58.1
-  mmc-utils 0.1+gitX (73d6c59af8d1...) -> 0.1+gitX (43282e80e174...)
-  mobile-broadband-provider-info 20201225 -> 20210805
-  mpg123 1.26.4 -> 1.28.2
-  mtd-utils 2.1.2 -> 2.1.3
-  mtools 4.0.26 -> 4.0.35
-  musl 1.2.2+gitX (e5d2823631bb...) -> 1.2.2+gitX (3f701faace7a...)
-  nativesdk-meson 0.57.1 -> 0.58.1
-  netbase 6.2 -> 6.3
-  nfs-utils 2.5.3 -> 2.5.4
-  ofono 1.31 -> 1.32
-  openssh 8.5p1 -> 8.7p1
-  opkg 0.4.4 -> 0.4.5
-  opkg-utils 0.4.3 -> 0.4.5
-  ovmf edk2-stable202102 -> edk2-stable202105
-  p11-kit 0.23.22 -> 0.24.0
-  pango 1.48.2 -> 1.48.9
-  patchelf 0.12 -> 0.13
-  perl 5.32.1 -> 5.34.0
-  piglit 1.0+gitrX (d4d9353b7290...) -> 1.0+gitrX (6a4be9e9946d...)
-  pkgconf 1.7.3 -> 1.8.0
-  powertop 2.13 -> 2.14
-  pseudo 1.9.0+gitX (b988b0a6b8af...) -> 1.9.0+gitX (0cda3ba5f94a...)
-  pulseaudio 14.2 -> 15.0
-  puzzles 0.0+gitX (84cb4c6701e0...) -> 0.0+gitX (8f3413c31ffd...)
-  python3 3.9.5 -> 3.9.6
-  python3-attrs 20.3.0 -> 21.2.0
-  python3-cython 0.29.22 -> 0.29.24
-  python3-dbus 1.2.16 -> 1.2.18
-  python3-dbusmock 0.22.0 -> 0.23.1
-  python3-docutils 0.16 -> 0.17.1
-  python3-git 3.1.14 -> 3.1.20
-  python3-gitdb 4.0.5 -> 4.0.7
-  python3-hypothesis 6.2.0 -> 6.15.0
-  python3-importlib-metadata 3.4.0 -> 4.6.4
-  python3-iniparse 0.4 -> 0.5
-  python3-jinja2 2.11.3 -> 3.0.1
-  python3-libarchive-c 2.9 -> 3.1
-  python3-magic 0.4.22 -> 0.4.24
-  python3-mako 1.1.4 -> 1.1.5
-  python3-markupsafe 1.1.1 -> 2.0.1
-  python3-more-itertools 8.7.0 -> 8.8.0
-  python3-numpy 1.20.1 -> 1.21.2
-  python3-packaging 20.9 -> 21.0
-  python3-pathlib2 2.3.5 -> 2.3.6
-  python3-pbr 5.4.4 -> 5.6.0
-  python3-pip 20.0.2 -> 21.2.4
-  python3-pluggy 0.13.1 -> 1.0.0
-  python3-pycairo 1.20.0 -> 1.20.1
-  python3-pygments 2.8.1 -> 2.10.0
-  python3-pygobject 3.38.0 -> 3.40.1
-  python3-pytest 6.2.2 -> 6.2.4
-  python3-scons 3.1.2 -> 4.2.0
-  python3-scons-native 3.1.2 -> 4.2.0
-  python3-setuptools 54.1.1 -> 57.4.0
-  python3-setuptools-scm 5.0.1 -> 6.0.1
-  python3-six 1.15.0 -> 1.16.0
-  python3-sortedcontainers 2.3.0 -> 2.4.0
-  python3-testtools 2.4.0 -> 2.5.0
-  python3-zipp 3.4.1 -> 3.5.0
-  qemu 5.2.0 -> 6.0.0
-  qemu-native 5.2.0 -> 6.0.0
-  qemu-system-native 5.2.0 -> 6.0.0
-  re2c 2.0.3 -> 2.2
-  rng-tools 6.11 -> 6.14
-  rpcbind 1.2.5 -> 1.2.6
-  rt-tests 1.10 -> 2.1
-  ruby 3.0.1 -> 3.0.2
-  rxvt-unicode 9.22 -> 9.26
-  shaderc 2020.5 -> 2021.1
-  shadow 4.8.1 -> 4.9
-  spirv-tools 2020.7 -> 2021.2
-  sqlite3 3.35.0 -> 3.36.0
-  squashfs-tools 4.4 -> 4.5
-  strace 5.11 -> 5.14
-  stress-ng 0.12.05 -> 0.13.00
-  sudo 1.9.6p1 -> 1.9.7p2
-  swig 3.0.12 -> 4.0.2
-  sysklogd 2.2.2 -> 2.2.3
-  systemd 247.6 -> 249.3
-  systemd-boot 247.6 -> 249.3
-  systemd-conf 247.6 -> 1.0
-  systemtap 4.4 -> 4.5
-  systemtap-native 4.4 -> 4.5
-  systemtap-uprobes 4.4 -> 4.5
-  tcf-agent 1.7.0+gitX (a022ef2f1acf...) -> 1.7.0+gitX (2735e3d6b7ec...)
-  texinfo 6.7 -> 6.8
-  tiff 4.2.0 -> 4.3.0
-  u-boot 2021.01 -> 2021.07
-  u-boot-tools 2021.01 -> 2021.07
-  usbutils 013 -> 014
-  util-linux 2.36.2 -> 2.37.2
-  util-linux-libuuid 2.36.2 -> 2.37.2
-  vala 0.50.4 -> 0.52.5
-  valgrind 3.16.1 -> 3.17.0
-  virglrenderer 0.8.2 -> 0.9.1
-  vte 0.62.2 -> 0.64.2
-  vulkan-headers 1.2.170.0 -> 1.2.182.0
-  vulkan-loader 1.2.170.0 -> 1.2.182.0
-  vulkan-samples git (55cebd9e7cc4...) -> git (d2187278cb66...)
-  vulkan-tools 1.2.170.0 -> 1.2.182.0
-  wayland-protocols 1.20 -> 1.21
-  webkitgtk 2.30.5 -> 2.32.3
-  wireless-regdb 2021.04.21 -> 2021.07.14
-  wpebackend-fdo 1.8.0 -> 1.10.0
-  x264 r3039+gitX (544c61f08219...) -> r3039+gitX (5db6aa6cab1b...)
-  xeyes 1.1.2 -> 1.2.0
-  xf86-input-libinput 0.30.0 -> 1.1.0
-  xkbcomp 1.4.4 -> 1.4.5
-  xkeyboard-config 2.32 -> 2.33
-  xorgproto 2020.1 -> 2021.4.99.2
-  xserver-xorg 1.20.10 -> 1.20.13
-  zstd 1.4.9 -> 1.5.0

Contributors to 3.4
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adam Romanek
-  Alejandro Hernandez Samaniego
-  Alexander Kanavin
-  Alexandre Belloni
-  Alexey Brodkin
-  Alex Stewart
-  Alistair Francis
-  Anatol Belski
-  Anders Wallin
-  Andrea Adami
-  Andreas Müller
-  Andrej Valek
-  Andres Beltran
-  Andrey Zhizhikin
-  Anibal Limon
-  Anthony Bagwell
-  Anton Blanchard
-  Anuj Mittal
-  Armin Kuster
-  Asfak Rahman
-  Bastian Krause
-  Bernhard Rosenkränzer
-  Bruce Ashfield
-  Carlos Rafael Giani
-  Chandana kalluri
-  Changhyeok Bae
-  Changqing Li
-  Chanho Park
-  Chen Qi
-  Chris Laplante
-  Christophe Chapuis
-  Christoph Muellner
-  Claudius Heine
-  Damian Wrobel
-  Daniel Ammann
-  Daniel Gomez
-  Daniel McGregor
-  Daniel Wagenknecht
-  Denys Dmytriyenko
-  Devendra Tewari
-  Diego Sueiro
-  Dmitry Baryshkov
-  Douglas Royds
-  Dragos-Marian Panait
-  Drew Moseley
-  Enrico Scholz
-  Fabio Berton
-  Florian Amstutz
-  Gavin Li
-  Guillaume Champagne
-  Harald Brinkmann
-  Henning Schild
-  He Zhe
-  Hongxu Jia
-  Hsia-Jun (Randy) Li
-  Jean Bouchard
-  Joe Slater
-  Jonas Höppner
-  Jon Mason
-  Jose Quaresma
-  Joshua Watt
-  Justin Bronder
-  Kai Kang
-  Kenfe-Mickael Laventure
-  Kevin Hao
-  Khairul Rohaizzat Jamaluddin
-  Khem Raj
-  Kiran Surendran
-  Konrad Weihmann
-  Kristian Klausen
-  Kyle Russell
-  Lee Chee Yang
-  Lei Maohui
-  Luca Boccassi
-  Marco Felsch
-  Marcus Comstedt
-  Marek Vasut
-  Mark Hatle
-  Markus Volk
-  Marta Rybczynska
-  Martin Jansa
-  Matthias Klein
-  Matthias Schiffer
-  Matt Madison
-  Matt Spencer
-  Max Krummenacher
-  Michael Halstead
-  Michael Ho
-  Michael Opdenacker
-  Mike Crowe
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Minjae Kim
-  Nicolas Dechesne
-  Niels Avonds
-  Nikolay Papenkov
-  Nisha Parrakat
-  Olaf Mandel
-  Oleksandr Kravchuk
-  Oleksandr Popovych
-  Oliver Kranz
-  Otavio Salvador
-  Patrick Williams
-  Paul Barker
-  Paul Eggleton
-  Paul Gortmaker
-  Paulo Cesar Zaneti
-  Peter Bergin
-  Peter Budny
-  Peter Kjellerstedt
-  Petr Vorel
-  Przemyslaw Gorszkowski
-  Purushottam Choudhary
-  Qiang Zhang
-  Quentin Schulz
-  Ralph Siemsen
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Rasmus Villemoes
-  Reto Schneider
-  Richard Purdie
-  Richard Weinberger
-  Robert Joslyn
-  Robert P. J. Day
-  Robert Yang
-  Romain Naour
-  Ross Burton
-  Sakib Sajal
-  Samuli Piippo
-  Saul Wold
-  Scott Murray
-  Scott Weaver
-  Stefan Ghinea
-  Stefan Herbrechtsmeier
-  Stefano Babic
-  Stefan Wiehler
-  Steve Sakoman
-  Teoh Jay Shen
-  Thomas Perrot
-  Tim Orling
-  Tom Pollard
-  Tom Rini
-  Tony Battersby
-  Tony Tascioglu
-  Trevor Gamblin
-  Trevor Woerner
-  Ulrich Ölmann
-  Valentin Danaila
-  Vinay Kumar
-  Vineela Tummalapalli
-  Vinícius Ossanes Aquino
-  Vivien Didelot
-  Vyacheslav Yurkov
-  Wang Mingyu
-  Wes Lindauer
-  William A. Kennington III
-  Yanfei Xu
-  Yann Dirson
-  Yi Fan Yu
-  Yi Zhao
-  Zang Ruochen
-  Zheng Ruoqin
-  Zoltan Boszormenyi

Repositories / Downloads for 3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`honister </poky/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4 </poky/tag/?h=yocto-3.4>`
-  Git Revision: :yocto_git:`f6d1126fff213460dc6954a5d5fc168606d76b66 </poky/commit/?id=f6d1126fff213460dc6954a5d5fc168606d76b66>`
-  Release Artefact:  poky-f6d1126fff213460dc6954a5d5fc168606d76b66
-  sha: 11e8f5760f704eed1ac37a5b09b1a831b5254d66459be75b06a72128c63e0411
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/poky-f6d1126fff213460dc6954a5d5fc168606d76b66.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/poky-f6d1126fff213460dc6954a5d5fc168606d76b66.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`honister </openembedded-core/log/?h=honister>`
-  Tag: :oe_git:`2021-10-honister </openembedded-core/tag/?h=2021-10-honister>`
-  Git Revision: :oe_git:`bb1dea6806f084364b6017db2567f438e805aef0 </openembedded-core/commit/?id=bb1dea6806f084364b6017db2567f438e805aef0>`
-  Release Artefact: oecore-bb1dea6806f084364b6017db2567f438e805aef0
-  sha: 9a356c407c567b1c26e535cad235204b0462cb79321fefb0844324a6020b31f4
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/oecore-bb1dea6806f084364b6017db2567f438e805aef0.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/oecore-bb1dea6806f084364b6017db2567f438e805aef0.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`honister </meta-mingw/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4 </meta-mingw/tag/?h=yocto-3.4>`
-  Git Revision: :yocto_git:`f5d761cbd5c957e4405c5d40b0c236d263c916a8 </meta-mingw/commit/?id=f5d761cbd5c957e4405c5d40b0c236d263c916a8>`
-  Release Artefact: meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8
-  sha: d4305d638ef80948584526c8ca386a8cf77933dffb8a3b8da98d26a5c40fcc11
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2

meta-intel

-  Repository Location: :yocto_git:`/meta-intel`
-  Branch: :yocto_git:`honister </meta-intel/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4 </meta-intel/tag/?h=yocto-3.4>`
-  Git Revision: :yocto_git:`90170cf85fe35b4e8dc00eee50053c0205276b63 </meta-intel/commit/?id=90170cf85fe35b4e8dc00eee50053c0205276b63>`
-  Release Artefact: meta-intel-90170cf85fe35b4e8dc00eee50053c0205276b63
-  sha: 2b3b43386dfcaaa880d819c1ae88b1251b55fb12c622af3d0936c3dc338491fc
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/meta-intel-90170cf85fe35b4e8dc00eee50053c0205276b63.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/meta-intel-90170cf85fe35b4e8dc00eee50053c0205276b63.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`honister </meta-gplv2/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4 </meta-gplv2/tag/?h=yocto-3.4>`
-  Git Revision: :yocto_git:`f04e4369bf9dd3385165281b9fa2ed1043b0e400 </meta-gplv2/commit/?id=f04e4369bf9dd3385165281b9fa2ed1043b0e400>`
-  Release Artefact: meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400
-  sha: ef8e2b1ec1fb43dbee4ff6990ac736315c7bc2d8c8e79249e1d337558657d3fe
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`1.52 </bitbake/log/?h=1.52>`
-  Tag: :oe_git:`2021-10-honister </bitbake/tag/?h=2021-10-honister>`
-  Git Revision: :oe_git:`c78ebac71ec976fdf27ea24767057882870f5c60 </bitbake/commit/?id=c78ebac71ec976fdf27ea24767057882870f5c60>`
-  Release Artefact: bitbake-c78ebac71ec976fdf27ea24767057882870f5c60
-  sha: 8077c7e7528cd73ef488ef74de3943ec66cae361459e5b630fb3cbe89c498d3d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/bitbake-c78ebac71ec976fdf27ea24767057882870f5c60.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/bitbake-c78ebac71ec976fdf27ea24767057882870f5c60.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`honister </yocto-docs/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4 </yocto-docs/tag/?h=yocto-3.4>`
-  Git Revision: :yocto_git:`d75c5450ecf56c8ac799a633ee9ac459e88f91fc </yocto-docs/commit/?id=d75c5450ecf56c8ac799a633ee9ac459e88f91fc>`

