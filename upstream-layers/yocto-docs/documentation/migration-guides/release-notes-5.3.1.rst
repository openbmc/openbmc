Release notes for Yocto-5.3.1 (Whinlatter)
------------------------------------------

Users of Alma 9, Rocky 9 and Centos Stream 9 rolling releases have seen obtuse failures in the execution of tar in various tasks after recent host distro updates. These newer versions of tar contain a CVE fix which uses a new glibc call/syscall (openat2). The fix is to update to a newer pseudo version which handles this syscall. This is not included in this stable release but we aim to include it in the next one.

Security Fixes in Yocto-5.3.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-11494`, :cve_nist:`2025-11839` and :cve_nist:`2025-11840`
-  cups: Fix :cve_nist:`2025-58436` and :cve_nist:`2025-61915`
-  dropbear: Fix :cve_nist:`2019-6111`
-  glib-2.0: Fix :cve_nist:`2025-13601`, :cve_nist:`2025-14087` and :cve_nist:`2025-14512`
-  gnutls: Fix :cve_nist:`2025-9820`
-  go: Fix :cve_nist:`2025-61727` and :cve_nist:`2025-61729`
-  libarchive: Fix :cve_nist:`2025-60753`
-  libpcap: Fix :cve_nist:`2025-11961` and :cve_nist:`2025-11964`
-  libpng: Fix :cve_nist:`2025-64505`, :cve_nist:`2025-64506`, :cve_nist:`2025-64720`,
   :cve_nist:`2025-65018` and :cve_nist:`2025-66293`
-  linux-yocto/6.12: Ignore :cve_nist:`2023-7324`, :cve_nist:`2024-57995`, :cve_nist:`2025-21833`,
   :cve_nist:`2025-22105`, :cve_nist:`2025-22107`, :cve_nist:`2025-22121`, :cve_nist:`2025-23129`,
   :cve_nist:`2025-23130`, :cve_nist:`2025-37803`, :cve_nist:`2025-37860`, :cve_nist:`2025-38643`,
   :cve_nist:`2025-38678`, :cve_nist:`2025-39678`, :cve_nist:`2025-39981`, :cve_nist:`2025-40014`,
   :cve_nist:`2025-40026`, :cve_nist:`2025-40027`, :cve_nist:`2025-40028`, :cve_nist:`2025-40029`,
   :cve_nist:`2025-40030`, :cve_nist:`2025-40031`, :cve_nist:`2025-40032`, :cve_nist:`2025-40033`,
   :cve_nist:`2025-40034`, :cve_nist:`2025-40035`, :cve_nist:`2025-40036`, :cve_nist:`2025-40037`,
   :cve_nist:`2025-40038`, :cve_nist:`2025-40039`, :cve_nist:`2025-40040`, :cve_nist:`2025-40041`,
   :cve_nist:`2025-40042`, :cve_nist:`2025-40043`, :cve_nist:`2025-40044`, :cve_nist:`2025-40045`,
   :cve_nist:`2025-40046`, :cve_nist:`2025-40047`, :cve_nist:`2025-40048`, :cve_nist:`2025-40049`,
   :cve_nist:`2025-40050`, :cve_nist:`2025-40051`, :cve_nist:`2025-40052`, :cve_nist:`2025-40053`,
   :cve_nist:`2025-40055`, :cve_nist:`2025-40056`, :cve_nist:`2025-40057`, :cve_nist:`2025-40058`,
   :cve_nist:`2025-40059`, :cve_nist:`2025-40060`, :cve_nist:`2025-40061`, :cve_nist:`2025-40062`,
   :cve_nist:`2025-40063`, :cve_nist:`2025-40066`, :cve_nist:`2025-40067`, :cve_nist:`2025-40068`,
   :cve_nist:`2025-40069`, :cve_nist:`2025-40070`, :cve_nist:`2025-40071`, :cve_nist:`2025-40072`,
   :cve_nist:`2025-40073`, :cve_nist:`2025-40076`, :cve_nist:`2025-40077`, :cve_nist:`2025-40078`,
   :cve_nist:`2025-40079`, :cve_nist:`2025-40080`, :cve_nist:`2025-40081`, :cve_nist:`2025-40082`,
   :cve_nist:`2025-40083`, :cve_nist:`2025-40084`, :cve_nist:`2025-40085`, :cve_nist:`2025-40087`,
   :cve_nist:`2025-40088`, :cve_nist:`2025-40089`, :cve_nist:`2025-40090`, :cve_nist:`2025-40091`,
   :cve_nist:`2025-40092`, :cve_nist:`2025-40093`, :cve_nist:`2025-40094`, :cve_nist:`2025-40095`,
   :cve_nist:`2025-40096`, :cve_nist:`2025-40097`, :cve_nist:`2025-40099`, :cve_nist:`2025-40100`,
   :cve_nist:`2025-40101`, :cve_nist:`2025-40103`, :cve_nist:`2025-40104`, :cve_nist:`2025-40105`,
   :cve_nist:`2025-40106`, :cve_nist:`2025-40107`, :cve_nist:`2025-40108`, :cve_nist:`2025-40109`,
   :cve_nist:`2025-40110`, :cve_nist:`2025-40111`, :cve_nist:`2025-40112`, :cve_nist:`2025-40115`,
   :cve_nist:`2025-40116`, :cve_nist:`2025-40117`, :cve_nist:`2025-40118`, :cve_nist:`2025-40119`,
   :cve_nist:`2025-40120`, :cve_nist:`2025-40121`, :cve_nist:`2025-40122`, :cve_nist:`2025-40123`,
   :cve_nist:`2025-40124`, :cve_nist:`2025-40125`, :cve_nist:`2025-40126`, :cve_nist:`2025-40127`,
   :cve_nist:`2025-40129`, :cve_nist:`2025-40131`, :cve_nist:`2025-40132`, :cve_nist:`2025-40133`,
   :cve_nist:`2025-40134`, :cve_nist:`2025-40137`, :cve_nist:`2025-40138`, :cve_nist:`2025-40140`,
   :cve_nist:`2025-40141`, :cve_nist:`2025-40142`, :cve_nist:`2025-40143`, :cve_nist:`2025-40144`,
   :cve_nist:`2025-40145`, :cve_nist:`2025-40148`, :cve_nist:`2025-40151`, :cve_nist:`2025-40152`,
   :cve_nist:`2025-40153`, :cve_nist:`2025-40154`, :cve_nist:`2025-40155`, :cve_nist:`2025-40156`,
   :cve_nist:`2025-40157`, :cve_nist:`2025-40159`, :cve_nist:`2025-40160`, :cve_nist:`2025-40161`,
   :cve_nist:`2025-40162`, :cve_nist:`2025-40163`, :cve_nist:`2025-40165`, :cve_nist:`2025-40166`,
   :cve_nist:`2025-40167`, :cve_nist:`2025-40169`, :cve_nist:`2025-40171`, :cve_nist:`2025-40172`,
   :cve_nist:`2025-40173`, :cve_nist:`2025-40174`, :cve_nist:`2025-40175`, :cve_nist:`2025-40176`,
   :cve_nist:`2025-40177`, :cve_nist:`2025-40178`, :cve_nist:`2025-40179`, :cve_nist:`2025-40180`,
   :cve_nist:`2025-40181`, :cve_nist:`2025-40182`, :cve_nist:`2025-40183`, :cve_nist:`2025-40184`,
   :cve_nist:`2025-40185`, :cve_nist:`2025-40186`, :cve_nist:`2025-40187`, :cve_nist:`2025-40188`,
   :cve_nist:`2025-40189`, :cve_nist:`2025-40190`, :cve_nist:`2025-40191`, :cve_nist:`2025-40192`,
   :cve_nist:`2025-40193`, :cve_nist:`2025-40194`, :cve_nist:`2025-40195`, :cve_nist:`2025-40196`,
   :cve_nist:`2025-40197`, :cve_nist:`2025-40198`, :cve_nist:`2025-40199`, :cve_nist:`2025-40200`,
   :cve_nist:`2025-40201`, :cve_nist:`2025-40202`, :cve_nist:`2025-40203`, :cve_nist:`2025-40204`,
   :cve_nist:`2025-40205`, :cve_nist:`2025-40206`, :cve_nist:`2025-40207`, :cve_nist:`2025-40208`,
   :cve_nist:`2025-40209`, :cve_nist:`2025-40211`, :cve_nist:`2025-40212` and :cve_nist:`2025-40213`
-  python3-urllib3: Fix :cve_nist:`2025-66418`
-  python3: Fix :cve_nist:`2025-6075` and :cve_nist:`2025-12084`
-  sqlite3: Fix :cve_nist:`2025-3277`, :cve_nist:`2025-6965` and :cve_nist:`2025-29087`


Fixes in Yocto-5.3.1
~~~~~~~~~~~~~~~~~~~~

bitbake
^^^^^^^
-  bin: Hide os.fork() deprecation warning in all bitbake scripts
-  bitbake-layers: Also hide os.fork() deprecation warning

meta-yocto
^^^^^^^^^^
-  poky.conf: bump version for 5.3.1

openembedded-core
^^^^^^^^^^^^^^^^^
-  build-appliance-image: Update to whinlatter head revisions
-  ccache: upgrade to 4.12.2
-  cross.bbclass: Propagate dependencies to outhash
-  cups: upgrade to 2.4.15
-  curl: Use host CA bundle by default for native(sdk) builds
-  cve-update: Avoid NFS caching issues
-  e2fsprogs: misc/create_inode.c: Fix for file larger than 2GB
-  ell: upgrade to 0.80
-  enchant2: upgrade to 2.8.14
-  glib-2.0: Upgrade to 2.86.1
-  glib-2.0: upgrade to 2.86.3
-  go: upgrade to 1.25.5
-  gst-devtools: upgrade to 1.26.7
-  gst-examples: upgrade to 1.26.7
-  gstreamer1.0: upgrade to 1.26.7
-  gstreamer1.0-libav: upgrade to 1.26.7
-  gstreamer1.0-plugins-bad: upgrade to 1.26.7
-  gstreamer1.0-plugins-base: upgrade to 1.26.7
-  gstreamer1.0-plugins-good: upgrade to 1.26.7
-  gstreamer1.0-plugins-ugly: upgrade to 1.26.7
-  gstreamer1.0-python: upgrade to 1.26.7
-  gstreamer1.0-rtsp-server: upgrade to 1.26.7
-  gstreamer1.0-vaapi: upgrade to 1.26.7
-  libarchive: upgrade to 3.8.3
-  libarchive: upgrade to 3.8.4
-  libpcap: upgrade to 1.10.6
-  libpng: upgrade to 1.6.52
-  libssh2: fix regression in KEX method validation (GH-1553)
-  libxmlb: upgrade to 0.3.24
-  linux-yocto/6.12: update to v6.12.60
-  llvm/clang: Upgrade to 21.1.7
-  mesa: upgrade to 25.2.8
-  python3: upgrade to 3.13.11
-  spdx30_tasks: Fix :term:`SPDX_CUSTOM_ANNOTATION_VARS` implementation
-  xserver-nodm-init: avoid race condition related to udev

yocto-docs
^^^^^^^^^^
-  Add the sphinx-copybutton extension
-  Fix bitbake version mapping for whinlatter
-  Makefile: fix rsvg-convert --format capitalization
-  brief-yoctoprojectqs/index.rst: fix improper code-block indentation
-  brief-yoctoprojectqs/index.rst: switch shell block to to console/text blocks
-  brief-yoctoprojectqs/index.rst: update available bitbake-setup configurations
-  brief-yoctoprojectqs: specify what "recent Ubuntu Linux distribution" is
-  dev-manual/limiting-resources.rst: update how to track pressure info
-  make sure Quick Build section and System Requirements are in sync
-  migration-guide: update 5.3 release notes download section
-  migration-guides/release-notes-5.3.rst: add contributors
-  migration-guides/release-notes-5.3.rst: add fixed cve
-  migration-guides/release-notes-5.3.rst: add license updates
-  migration-guides/release-notes-5.3.rst: add recipe upgrades
-  migration-guides/release-notes-5.3.rst: latest changes from master
-  overview-manual/concepts.rst: update the cross-development toolchain section
-  poky.yaml.in: add DISTRO_RELEASE_SERIES
-  ref-manual/classes.rst: document the image-container class
-  ref-manual/faq.rst: add Q&A on third-party vuln scanning tools
-  ref-manual/system-requirements.rst: add RockyLinux install instructions
-  ref-manual/system-requirements.rst: fix AlmaLinux PDF build
-  ref-manual/tasks.rst: document the do_list_image_features task
-  ref-manual/variables.rst: document WESTON_USER/WESTON_USER_HOME variables
-  ref-manual: Document :term:`SPDX` 3.0.1 variables
-  set_versions.py: add wrynose as devbranch
-  tools/build-docs-container: add CentOS Stream 9 support
-  tools/build-docs-container: move container files in their own directory
-  tools: add gen-cve-release-notes
-  tools: ubuntu_docs: remove duplicate python3-saneyaml


Known Issues in Yocto-5.3.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.3.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Alexander Kanavin
-  Ankur Tyagi
-  Antonin Godard
-  Bruce Ashfield
-  Chen Qi
-  Deepesh Varatharajan
-  Dmitry Baryshkov
-  Gyorgy Sarvari
-  Jayasurya Maganuru
-  Jörg Sommer
-  Lee Chee Yang
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Moritz Haase
-  Paul Barker
-  Peter Marko
-  Quentin Schulz
-  Robert Yang
-  Stefano Tondo
-  Vijay Anusuri
-  Wang Mingyu
-  Yash Shinde
-  Yoann Congal
-  Zhang Peng


Repositories / Downloads for Yocto-5.3.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`whinlatter </yocto-docs/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.1 </yocto-docs/log/?h=yocto-5.3.1>`
-  Git Revision: :yocto_git:`102a33294e63a5581c413555040f790161fc80ff </yocto-docs/commit/?id=102a33294e63a5581c413555040f790161fc80ff>`
-  Release Artefact: yocto-docs-102a33294e63a5581c413555040f790161fc80ff
-  sha: 377b828c5dbf82b8a918360a52ff7b4122d37fd8d13d0451738edd57a1924083
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.1/yocto-docs-102a33294e63a5581c413555040f790161fc80ff.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3.1/yocto-docs-102a33294e63a5581c413555040f790161fc80ff.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`whinlatter </openembedded-core/log/?h=whinlatter>`
-  Tag:  :oe_git:`yocto-5.3.1 </openembedded-core/log/?h=yocto-5.3.1>`
-  Git Revision: :oe_git:`dd10706cfafb5574b7cf316fca2300d166ef71b0 </openembedded-core/commit/?id=dd10706cfafb5574b7cf316fca2300d166ef71b0>`
-  Release Artefact: oecore-dd10706cfafb5574b7cf316fca2300d166ef71b0
-  sha: b3182231a4a10f57215289b0f42ebe658ee9b1ed0b0bfe414d846a778ff7c598
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.1/oecore-dd10706cfafb5574b7cf316fca2300d166ef71b0.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3.1/oecore-dd10706cfafb5574b7cf316fca2300d166ef71b0.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`whinlatter </meta-yocto/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.1 </meta-yocto/log/?h=yocto-5.3.1>`
-  Git Revision: :yocto_git:`6973ca663aaa9c3ab517ee960ab7985a5bf54c07 </meta-yocto/commit/?id=6973ca663aaa9c3ab517ee960ab7985a5bf54c07>`
-  Release Artefact: meta-yocto-6973ca663aaa9c3ab517ee960ab7985a5bf54c07
-  sha: 0e126b092e74bb217416d9603002b20db8e552b45b23d634cdae955fd089dfe2
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.1/meta-yocto-6973ca663aaa9c3ab517ee960ab7985a5bf54c07.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3.1/meta-yocto-6973ca663aaa9c3ab517ee960ab7985a5bf54c07.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`whinlatter </meta-mingw/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.1 </meta-mingw/log/?h=yocto-5.3.1>`
-  Git Revision: :yocto_git:`00323de97e397d4f6734ef2191806616989f5e10 </meta-mingw/commit/?id=00323de97e397d4f6734ef2191806616989f5e10>`
-  Release Artefact: meta-mingw-00323de97e397d4f6734ef2191806616989f5e10
-  sha: c9a70539b12c0642596fde6a2766d4a6a8fec8b2a366453fb6473363127a1c77
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.1/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3.1/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.16 </bitbake/log/?h=2.16>`
-  Tag:  :oe_git:`yocto-5.3.1 </bitbake/log/?h=yocto-5.3.1>`
-  Git Revision: :oe_git:`663021740bc086bd959a8457ad9ddb6da52a8278 </bitbake/commit/?id=663021740bc086bd959a8457ad9ddb6da52a8278>`
-  Release Artefact: bitbake-663021740bc086bd959a8457ad9ddb6da52a8278
-  sha: a290072317e5c533fafd18608e61c9ebe9acf24d2ce46b43ba99df42c77e7073
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.1/bitbake-663021740bc086bd959a8457ad9ddb6da52a8278.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3.1/bitbake-663021740bc086bd959a8457ad9ddb6da52a8278.tar.bz2


