Release notes for Yocto-5.1.4 (Styhead)
---------------------------------------

Security Fixes in Yocto-5.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-0840`
-  grub: Fix :cve_nist:`2024-45774`, :cve_nist:`2024-45775`, :cve_nist:`2024-45776`,
   :cve_nist:`2024-45777`, :cve_nist:`2024-45778`, :cve_nist:`2024-45779`, :cve_nist:`2024-45780`,
   :cve_nist:`2024-45781`, :cve_nist:`2024-45782`, :cve_nist:`2024-45783`, :cve_nist:`2024-56737`,
   :cve_nist:`2025-0622`, :cve_nist:`2025-0624`, :cve_nist:`2025-0677`, :cve_nist:`2025-0678`,
   :cve_nist:`2025-0684`, :cve_nist:`2025-0685`, :cve_nist:`2025-0686`, :cve_nist:`2025-0689`,
   :cve_nist:`2025-0690`, :cve_nist:`2025-1118` and :cve_nist:`2025-1125`
-  libtasn1: fix :cve_nist:`2024-12133`
-  libxml2: fix :cve_nist:`2024-56171`, :cve_nist:`2025-24928` and :cve_nist:`2025-27113`
-  openssh: Fix :cve_nist:`2025-26465` and :cve_nist:`2025-26466`
-  puzzles: Ignore :cve_nist:`2024-13769`, :cve_nist:`2024-13770` and :cve_nist:`2025-0837`
-  subversion: Ignore :cve_nist:`2024-45720`
-  xserver-xorg: Fix :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`,
   :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600`
   and :cve_nist:`2025-26601`
-  xwayland: Fix :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`,
   :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600`
   and :cve_nist:`2025-26601`


Fixes in Yocto-5.1.4
~~~~~~~~~~~~~~~~~~~~

-  bitbake: event/utils: Avoid deadlock from lock_timeout() and recursive events
-  bitbake: utils: Add signal blocking for lock_timeout
-  bitbake: utils: Print information about lock issue before exiting
-  bitbake: utils: Tweak lock_timeout logic
-  build-appliance-image: Update to styhead head revision
-  docs: Remove all mention of core-image-lsb
-  grub: backport strlcpy function
-  grub: drop obsolete CVE statuses
-  icu: Adjust ICU_DATA_DIR path on big endian targets
-  libtasn1: upgrade to 4.20.0
-  libxml2: upgrade to 2.13.6
-  migration-guides: add release notes for 4.0.25 and 5.1.3
-  poky.conf: bump version for 5.1.4
-  ref-manual: Add missing variable :term:`IMAGE_ROOTFS_MAXSIZE`
-  ref-manual: don't refer to poky-lsb
-  ref-manual: remove OE_IMPORTS
-  tzcode-native: Fix compiler setting from 2023d version
-  tzdata/tzcode-native: upgrade to 2025a
-  vulnerabilities/classes: remove references to cve-check text format
-  xserver-xf86-config: add a configuration fragment to disable screen blanking
-  xserver-xf86-config: remove obsolete configuration files
-  xserver-xorg: upgrade to 21.1.16
-  xwayland: upgrade to 21.1.6


Known Issues in Yocto-5.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  NA

Contributors to Yocto-5.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~


Thanks to the following people who contributed to this release:

-  Alessio Cascone
-  Lee Chee Yang
-  Makarios Christakis
-  Marta Rybczynska
-  Peter Marko
-  Priyal Doshi
-  Richard Purdie
-  Ross Burton
-  Steve Sakoman
-  Vijay Anusuri
-  Wang Mingyu
-  Weisser, Pascal


Repositories / Downloads for Yocto-5.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`styhead </poky/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.4 </poky/log/?h=yocto-5.1.4>`
-  Git Revision: :yocto_git:`70dc28ac287bf35541270cae1d99130a0f6b7b5f </poky/commit/?id=70dc28ac287bf35541270cae1d99130a0f6b7b5f>`
-  Release Artefact: poky-70dc28ac287bf35541270cae1d99130a0f6b7b5f
-  sha: 63f1d3d47a28bd9b41c89db6e1f2657c04233a00d10210795e766c0bc265d766
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.4/poky-70dc28ac287bf35541270cae1d99130a0f6b7b5f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.4/poky-70dc28ac287bf35541270cae1d99130a0f6b7b5f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`styhead </openembedded-core/log/?h=styhead>`
-  Tag:  :oe_git:`yocto-5.1.4 </openembedded-core/log/?h=yocto-5.1.4>`
-  Git Revision: :oe_git:`2d94f4b8a852dc761f89e5106347e239382df5fb </openembedded-core/commit/?id=2d94f4b8a852dc761f89e5106347e239382df5fb>`
-  Release Artefact: oecore-2d94f4b8a852dc761f89e5106347e239382df5fb
-  sha: 344ac23f814c049d69b06cee42c43b7b422506ce84397406caef09becb2555bf
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.4/oecore-2d94f4b8a852dc761f89e5106347e239382df5fb.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.4/oecore-2d94f4b8a852dc761f89e5106347e239382df5fb.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`styhead </meta-mingw/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.4 </meta-mingw/log/?h=yocto-5.1.4>`
-  Git Revision: :yocto_git:`77fe18d4f8ec34501045c5d92ce7e13b1bd129e9 </meta-mingw/commit/?id=77fe18d4f8ec34501045c5d92ce7e13b1bd129e9>`
-  Release Artefact: meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9
-  sha: 4c7f8100a3675d9863e51825def3df5b263ffc81cd57bae26eedbc156d771534
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.4/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.4/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.10 </bitbake/log/?h=2.10>`
-  Tag:  :oe_git:`yocto-5.1.4 </bitbake/log/?h=yocto-5.1.4>`
-  Git Revision: :oe_git:`82b9f42126983579da03bdbb4e3ebf07346118a7 </bitbake/commit/?id=82b9f42126983579da03bdbb4e3ebf07346118a7>`
-  Release Artefact: bitbake-82b9f42126983579da03bdbb4e3ebf07346118a7
-  sha: 209d62c5262f2287af60e7fe2343c29ab25b5088de4da71de89016e75900285a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.4/bitbake-82b9f42126983579da03bdbb4e3ebf07346118a7.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.4/bitbake-82b9f42126983579da03bdbb4e3ebf07346118a7.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`styhead </yocto-docs/log/?h=styhead>`
-  Tag: :yocto_git:`yocto-5.1.4 </yocto-docs/log/?h=yocto-5.1.4>`
-  Git Revision: :yocto_git:`f0324b8f14881227336f84325cdebd0518e17796 </yocto-docs/commit/?id=f0324b8f14881227336f84325cdebd0518e17796>`

