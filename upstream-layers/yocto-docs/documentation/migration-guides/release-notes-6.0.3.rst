.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-6.0.3 (Wrynose)
---------------------------------------

Security Fixes in Yocto-6.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2026-11331`, :cve_nist:`2026-11721`, :cve_nist:`2026-13321`,
   :cve_nist:`2026-10723`, :cve_nist:`2026-12617` and :cve_nist:`2026-10822`
-  binutils: Fix :cve_nist:`2026-6846`
-  bzip2: Fix :cve_nist:`2026-42250`
-  cargo: Fix :cve_nist:`2026-5222` and :cve_nist:`2026-5223`
-  cups: Fix :cve_nist:`2026-27447` and :cve_nist:`2026-41079`
-  curl: Fix :cve_nist:`2026-5545`, :cve_nist:`2026-5773`, :cve_nist:`2026-6253`, :cve_nist:`2026-6429`
   and :cve_nist:`2026-7168`
-  curl: Ignore :cve_nist:`2026-10536` if nghttp2 not enabled
-  dhcpcd: Fix :cve_nist:`2026-56113`, :cve_nist:`2026-56114`, :cve_nist:`2026-56116` and
   :cve_nist:`2026-56117`
-  expat: Fix :cve_nist:`2026-41080`, :cve_nist:`2026-45186`, :cve_nist:`2026-56132`,
   :cve_nist:`2026-56403`, :cve_nist:`2026-56404`, :cve_nist:`2026-56405`, :cve_nist:`2026-56406`,
   :cve_nist:`2026-56407`, :cve_nist:`2026-56408`, :cve_nist:`2026-56409`, :cve_nist:`2026-56410`
   and :cve_nist:`2026-56411`
-  gawk: Fix :cve_nist:`2026-40467`, :cve_nist:`2026-40468`, :cve_nist:`2026-40469` and
   :cve_nist:`2026-40553`
-  glib-2.0: Fix :cve_nist:`2026-58010`, :cve_nist:`2026-58011`, :cve_nist:`2026-58012`,
   :cve_nist:`2026-58013`, :cve_nist:`2026-58014`, :cve_nist:`2026-58015` and :cve_nist:`2026-58016`
-  gnupg: Fix :cve_nist:`2026-57062`
-  gnutls: Fix :cve_nist:`2026-3832`, :cve_nist:`2026-3833`, :cve_nist:`2026-42009` and
   :cve_nist:`2026-33846`
-  go: Fix :cve_nist:`2026-39822` and :cve_nist:`2026-42505`
-  gstreamer1.0: Ignore :cve_nist:`2026-5056`
-  gzip: Fix :cve_nist:`2026-41991` and :cve_nist:`2026-41992`
-  libcap: Fix :cve_nist:`2026-4878`
-  libgcrypt: Fix :cve_nist:`2026-41989` and :cve_nist:`2026-41990`
-  libpng: Fix :cve_nist:`2026-34757`
-  libpng: Ignore :cve_nist:`2026-40930`
-  libsolv: Fix :cve_nist:`2026-9149`
-  libssh2: Fix :cve_nist:`2025-15661`, :cve_nist:`2026-55199` and :cve_nist:`2026-55200`
-  libxml2: Fix :cve_nist:`2026-11979`
-  libxpm: Fix :cve_nist:`2026-4367`
-  openssh: Fix :cve_nist:`2026-59995`, :cve_nist:`2026-59996`, :cve_nist:`2026-59997`,
   :cve_nist:`2026-59999`, :cve_nist:`2026-60000`, :cve_nist:`2026-60001` and :cve_nist:`2026-60002`
-  openssh: Ignore :cve_nist:`2026-59998` if kerberos not enabled
-  openssh: Ignore :cve_nist:`2026-3497`
-  perl: Fix :cve_nist:`2026-8376`
-  python3: Fix :cve_nist:`2026-3276`, :cve_nist:`2026-7210`, :cve_nist:`2026-7774`,
   :cve_nist:`2026-8328`, :cve_nist:`2026-9669`, :cve_nist:`2026-11940` and :cve_nist:`2026-11972`
-  python3-setuptools: Fix :cve_nist:`2026-59890`
-  python3-urllib3: Fix :cve_nist:`2026-44431`
-  qemu: Fix :cve_nist:`2025-14876`, :cve_nist:`2026-0665` and :cve_nist:`2026-2243`
-  socat: Fix :cve_nist:`2026-56123`
-  sqlite3: Fix :cve_nist:`2026-11822` and :cve_nist:`2026-11824`
-  tar: Fix :cve_nist:`2026-5704`
-  util-linux: Fix :cve_nist:`2026-13595` and :cve_nist:`2026-27456`
-  vim: Fix :cve_nist:`2026-41411`, :cve_nist:`2026-42307`, :cve_nist:`2026-43961`,
   :cve_nist:`2026-44656`, :cve_nist:`2026-45130`, :cve_nist:`2026-46483`, :cve_nist:`2026-47162`,
   :cve_nist:`2026-47167`, :cve_nist:`2026-52858`, :cve_nist:`2026-52859`, :cve_nist:`2026-52860`,
   :cve_nist:`2026-55892` and :cve_nist:`2026-57452`


Fixes in Yocto-6.0.3
~~~~~~~~~~~~~~~~~~~~

bitbake
^^^^^^^

-  bitbake-setup: always write newline at end of the README
-  utils: Add NFS EEXISTS/isdir failure workaround

meta-yocto
^^^^^^^^^^

-  README: Add wrynose subject-prefix to git-send-email suggestion
-  b4-config: add send-prefixes for wrynose
-  poky.conf: Bump version for 6.0.3 release
-  yocto-bsps: update to v6.18.39

openembedded-core
^^^^^^^^^^^^^^^^^

-  baremetal-helloworld: Fix override order
-  bc: set :term:`CVE_PRODUCT`
-  bind: upgrade to 9.20.26
-  bluez5: Fix sending extra bytes with MGMT_OP_ADD_EXT_ADV_DATA
-  bluez5: fix gatt cache sync issue
-  bluez5: fix set volume failure
-  bluez5: set L2CAP IMTU for OBEX profile listeners
-  build-appliance-image: Update to wrynose head revisions
-  clang/llvm: Upgrade to 22.1.8
-  cmake-native: prevent host libidn2 contamination
-  common-licenses/PHP-3.0: Correct the license text
-  cpan_build: disable .packlist and html doc
-  create-spdx-image-3.0: correct :term:`SSTATE_SKIP_CREATION` key for do_create_image_sbom_spdx
-  devtool: provide explicit error for missing "script" command
-  dmidecode: fix x86-only tools being installed on ARM targets
-  dropbear: Add missing WTFPL & Unlicense in :term:`LICENSE` and :term:`LIC_FILES_CHKSUM`
-  flac: fix buildpaths with doxygen enabled
-  flac: make documentation build deterministic
-  gdb: Upgrade to 17.2
-  glib-2.0: upgrade to 2.88.2
-  go: upgrade to 1.26.5
-  i2c-tools: add LGPL-2.1-or-later license for libi2c
-  kernel-fit-image.bbclass: Do not include kernel property in DTBO config subnodes
-  kernel-fit-image.bbclass: Fix operation with :term:`KERNEL_DTBVENDORED` = "1"
-  kernel-fit-image: Add :term:`KERNEL_DTBVENDORED` support for :term:`FIT_CONF_DEFAULT_DTB`
-  libjitterentropy: fix license statement
-  libxpm: upgrade to 3.5.19
-  linux-yocto/6.18: drm/virtio: fix deadlock in display_info_cb by removing hotplug from dequeue
   worker
-  linux-yocto/6.18: qat/intel configuration warning fixes
-  linux-yocto/6.18: update to v6.18.39
-  nospdx: also drop do_create_recipe_sbom
-  oe-selftest: fitimage: Add tests for :term:`KERNEL_DTBVENDORED`
-  oe-selftest: fitimage: Do not expect kernel property in DTBO config subnodes
-  opensbi: don't override ELFFLAGS, silence ldflags QA for bare-metal ELFs
-  ovmf: fix tpm :term:`PACKAGECONFIG` to use TPM2_ENABLE
-  package.bbclass: hardcode emit_pkgdata to run last
-  ptest-packagelists.inc: disable glib-2.0 for RISCV64
-  python3: skiptest tracemalloc_track_race
-  python3: upgrade to 3.14.6
-  python3-certifi: set :term:`CVE_PRODUCT`
-  python3-cryptography: set :term:`CVE_PRODUCT`
-  python3-idna: set :term:`CVE_PRODUCT`
-  python3-pip: set :term:`CVE_PRODUCT`
-  python3-ply: set :term:`CVE_PRODUCT`
-  python3-pyasn1: set :term:`CVE_PRODUCT`
-  python3-pyopenssl: set :term:`CVE_PRODUCT`
-  python3-pyyaml: set :term:`CVE_PRODUCT`
-  python3-xmltodict: set :term:`CVE_PRODUCT`
-  qemuboot.bbclass: add missing task dependency on kernel deploy
-  quota: use native rpcgen and a single-word RPCGEN_CPP
-  rootfs: move tasks using image_list_installed_packages to postuninstall
-  scripts/install-buildtools: Update to 6.0.2
-  selftest: uboot: remove duplicated KVM presence test
-  socat: upgrade to 1.8.1.3
-  sstate: Reduce native sysroot execution race potential
-  strace: remove skip-bpf.patch
-  sudo: fix pam-wheel sed for sudo 1.9.17p2 sudoers
-  systemd-systemctl-native: disable libpam meson option
-  tcl: disable the timer tests in run-ptest
-  texinfo: add missing perl module runtime dependencies
-  tzdata/tzcode-native: upgrade to 2026c
-  u-boot: re-enable RISC-V compressed (c) ISA extension
-  util-linux: upgrade to 2.41.5
-  vex: remove obsolete semicolon
-  wic: upgrade to 0.3.1
-  xmlto: correct srcrev to point to released version
-  xmlto: update :term:`SRC_URI`

yocto-docs
^^^^^^^^^^

-  brief-yoctoprojectqs/index.rst: fix a replacement typo
-  brief-yoctoprojectqs/index.rst: refresh bitbake-setup outputs
-  brief-yoctoprojectqs/index.rst: use pip to install bitbake-setup
-  bsp-guide/bsp.rst: fix raspberry layer content example
-  contributor-guide: Note patch complexity requirements for stable branches
-  dev-manual/creating-fragments.rst: fix typos
-  dev-manual/wic.rst: add a requirement to use wic from the build system
-  dev-manual/wic.rst: add where to set IMAGE_FSTYPES/WKS_FILE[S] from
-  dev-manual/wic.rst: convert code snippet to code-blocks
-  dev-manual/wic.rst: remove bullet point on host requirements
-  dev-manual/wic.rst: remove note on wic-tools
-  dev-manual/wic.rst: replace deprecated wks directories
-  dev-manual: Fix missing whitespace around '=' operator
-  dev-manual: update bmaptool section, refer to "bmaptool" package
-  docs-wide: fix broken path links
-  docs-wide: fix various broken links
-  docs-wide: remove CROPS references
-  migration-guide: 5.1: Fix typo
-  migration-guide: add release notes for 5.0.19 and 6.0.2
-  migration-guides/migration-3.4.rst: replace rlbl broken link
-  migration-guides/migration-6.0.rst: fix wks file move note
-  migration-guides/release-notes-3.4.2.rst: fix a broken link
-  migration-guides/release-notes-5.0.rst: remove broken link
-  migration-guides: add add release notes for 6.0.1
-  migration-guides: replace broken link with archive links
-  recipe-style-guide: Clarify when License-Update tag is needed
-  ref-manual/classes.rst: replace deprecated wks directory
-  ref-manual/classes.rst: replace obsolete mailing list thread
-  ref-manual/images.rst: update obsolete VMWare links
-  ref-manual/kickstart.rst: document the include directive
-  ref-manual/kickstart.rst: remove note on available commands
-  ref-manual/kickstart.rst: replace deprecated wks directory
-  ref-manual/release-process.rst: update LTS supported versions
-  ref-manual/variables.rst: document missing CONFLICT_*_FEATURES variables
-  ref-manual/variables.rst: document the :term:`CCACHE_NATIVE_RECIPES_ALLOWED` variable
-  ref-manual/variables.rst: document the IMAGE_*_DEBUGFS variables
-  ref-manual/variables.rst: document the :term:`LOCALE_PATHS` variable
-  ref-manual/variables.rst: document the LOCALE_UTF8_IS_DEFAULT variable
-  ref-manual/variables.rst: document the :term:`QB_DEFAULT_BIOS` variable
-  ref-manual/variables.rst: document the :term:`TEST_SERIALCONTROL_CONNECT_TIMEOUT` variable
-  ref-manual/variables.rst: document the TEST_SERIALCONTROL_PS1 variable
-  ref-manual: Fix occurrences of omitted space with :prepend
-  ref-manual: add "KERNEL_IMAGE_STRIP_EXTRA_SECTIONS" to variables
-  ref-manual: expand on kernel "do_sizecheck" task
-  ref-manual: fix incorrect heading level for site.conf section
-  ref-manual: remove all traces of "kernel_menuconfig" task
-  ref-manual: under meta/files entry, mention Wic files


Known Issues in Yocto-6.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-6.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~


Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Adrian Freihofer
-  Alexander Kanavin
-  Amaury Couderc
-  Anil Dongare
-  Anton Skorup
-  Antonin Godard
-  AshishKumar Mishra
-  Ashishkumar Parmar
-  Benjamin Robin (Schneider Electric)
-  Bruce Ashfield
-  Daniel Turull
-  Darsh Kelaiya
-  David Nyström
-  Deepak Rathore
-  Deepesh Varatharajan
-  Devansh Patel
-  Eilís 'pidge' Ní Fhlannagáin
-  Eric Meyers
-  Ernest Van Hoecke
-  Gustavo Henrique Nihei
-  Hiago De Franco
-  Himanshu Jadon
-  Hitendra Prajapati
-  Jaipaul Cheernam
-  Jinwang Li
-  João Marcos Costa
-  Khem Raj
-  Kris Gavvala
-  Lee Chee Yang
-  Leonid Iziumtsev
-  Marek Vasut
-  Mengshi Wu
-  Nate Kent
-  Niko Mauno
-  Otavio Salvador
-  Paul Barker
-  Peter Kjellerstedt
-  Peter Marko
-  Pritam Srichandan Sahoo
-  Richard Purdie
-  Robert P. J. Day
-  Roland Kovacs
-  Ross Burton
-  Ryan Eatmon
-  Sai Sneha
-  Shubham Pushpkar
-  Siddharth Doshi
-  Siva Balasubramanian
-  Sudhir Dumbhare
-  Theo Gaige (Schneider Electric)
-  Ulrich Ölmann
-  Vijay Anusuri
-  Walter Werner Schneider
-  Wei Deng
-  Wei Gao
-  Wes Malone
-  Xiaozhan Li
-  Xiuzhuo Shang
-  Yoann Congal
-  jaekyu.lee
-  mark.yang


Repositories / Downloads for Yocto-6.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`wrynose </yocto-docs/log/?h=wrynose>`
-  Tag:  :yocto_git:`yocto-6.0.3 </yocto-docs/log/?h=yocto-6.0.3>`
-  Git Revision: :yocto_git:`5b1ff88511d90634bb3077b878176de577464e12 </yocto-docs/commit/?id=5b1ff88511d90634bb3077b878176de577464e12>`
-  Release Artefact: yocto-docs-5b1ff88511d90634bb3077b878176de577464e12
-  sha: f686c225af0ef33d4607dc03f090c77b0963dc8f15d750c886004786f4297454
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.3/yocto-docs-5b1ff88511d90634bb3077b878176de577464e12.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.3/yocto-docs-5b1ff88511d90634bb3077b878176de577464e12.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`wrynose </openembedded-core/log/?h=wrynose>`
-  Tag:  :oe_git:`yocto-6.0.3 </openembedded-core/log/?h=yocto-6.0.3>`
-  Git Revision: :oe_git:`ef022bf82d79015802309d14c28b13373ebe53f5 </openembedded-core/commit/?id=ef022bf82d79015802309d14c28b13373ebe53f5>`
-  Release Artefact: oecore-ef022bf82d79015802309d14c28b13373ebe53f5
-  sha: f43213e9e658cbf3d3c1fd1fe6b68e1a915eb5aec43a0b568387752a9948955e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.3/oecore-ef022bf82d79015802309d14c28b13373ebe53f5.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.3/oecore-ef022bf82d79015802309d14c28b13373ebe53f5.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`wrynose </meta-yocto/log/?h=wrynose>`
-  Tag:  :yocto_git:`yocto-6.0.3 </meta-yocto/log/?h=yocto-6.0.3>`
-  Git Revision: :yocto_git:`1b132647002eea43a2c7a7f857f63f42dacbc26c </meta-yocto/commit/?id=1b132647002eea43a2c7a7f857f63f42dacbc26c>`
-  Release Artefact: meta-yocto-1b132647002eea43a2c7a7f857f63f42dacbc26c
-  sha: 15fa3132056f68e0f4188193bea2ee28dada614ef432a1b2c306d8b1d8141888
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.3/meta-yocto-1b132647002eea43a2c7a7f857f63f42dacbc26c.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.3/meta-yocto-1b132647002eea43a2c7a7f857f63f42dacbc26c.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.18 </bitbake/log/?h=2.18>`
-  Tag:  :oe_git:`yocto-6.0.3 </bitbake/log/?h=yocto-6.0.3>`
-  Git Revision: :oe_git:`fae9db3168dbff1b8c76fe9c6726a9687ff97514 </bitbake/commit/?id=fae9db3168dbff1b8c76fe9c6726a9687ff97514>`
-  Release Artefact: bitbake-fae9db3168dbff1b8c76fe9c6726a9687ff97514
-  sha: 3e338310f008dfb9e8e26a22f44fdf89aa72188acc549905a5cf70a7c4797310
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.3/bitbake-fae9db3168dbff1b8c76fe9c6726a9687ff97514.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.3/bitbake-fae9db3168dbff1b8c76fe9c6726a9687ff97514.tar.bz2

