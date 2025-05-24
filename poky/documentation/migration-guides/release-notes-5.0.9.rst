.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.9 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2024-57360`, :cve_nist:`2025-1176`, :cve_nist:`2025-1178` and
   :cve_nist:`2025-1181`
-  expat: Fix :cve_nist:`2024-8176`
-  freetype: Fix :cve_nist:`2025-27363`
-  ghostscript: Fix :cve_nist:`2025-27830`, :cve_nist:`2025-27831`, :cve_nist:`2025-27832`,
   :cve_nist:`2025-27833`, :cve_nist:`2025-27833`, :cve_nist:`2025-27834`, :cve_nist:`2025-27835`
   and :cve_nist:`2025-27836`
-  go: fix :cve_nist:`2025-22870` and :cve_nist:`2025-22871`
-  grub: Fix :cve_nist:`2024-45781`, :cve_nist:`2024-45774`, :cve_nist:`2024-45775`,
   :cve_nist:`2024-45776`, :cve_nist:`2024-45777`, :cve_nist:`2024-45778`, :cve_nist:`2024-45779`,
   :cve_nist:`2024-45780`, :cve_nist:`2024-45782`, :cve_nist:`2024-45783`, :cve_nist:`2024-56737`,
   :cve_nist:`2025-0622`, :cve_nist:`2025-0624`, :cve_nist:`2025-0677`, :cve_nist:`2025-0678`,
   :cve_nist:`2025-0684`, :cve_nist:`2025-0685`, :cve_nist:`2025-0686`, :cve_nist:`2025-0689`,
   :cve_nist:`2025-0690`, :cve_nist:`2025-1118` and :cve_nist:`2025-1125`
-  libarchive: Fix :cve_nist:`2024-20696`, :cve_nist:`2024-48957`, :cve_nist:`2024-48958`,
   :cve_nist:`2025-1632` and :cve_nist:`2025-25724`
-  libxslt: Fix :cve_nist:`2024-24855` and :cve_nist:`2024-55549`
-  linux-yocto/6.6: Fix :cve_nist:`2024-54458`, :cve_nist:`2024-57834`, :cve_nist:`2024-57973`,
   :cve_nist:`2024-57978`, :cve_nist:`2024-57979`, :cve_nist:`2024-57980`, :cve_nist:`2024-57981`,
   :cve_nist:`2024-57984`, :cve_nist:`2024-57996`, :cve_nist:`2024-57997`, :cve_nist:`2024-58002`,
   :cve_nist:`2024-58005`, :cve_nist:`2024-58007`, :cve_nist:`2024-58010`, :cve_nist:`2024-58011`,
   :cve_nist:`2024-58013`, :cve_nist:`2024-58017`, :cve_nist:`2024-58020`, :cve_nist:`2024-58034`,
   :cve_nist:`2024-58052`, :cve_nist:`2024-58055`, :cve_nist:`2024-58058`, :cve_nist:`2024-58063`,
   :cve_nist:`2024-58068`, :cve_nist:`2024-58069`, :cve_nist:`2024-58070`, :cve_nist:`2024-58071`,
   :cve_nist:`2024-58076`, :cve_nist:`2024-58080`, :cve_nist:`2024-58083`, :cve_nist:`2024-58088`,
   :cve_nist:`2025-21700`, :cve_nist:`2025-21703`, :cve_nist:`2025-21707`, :cve_nist:`2025-21711`,
   :cve_nist:`2025-21715`, :cve_nist:`2025-21716`, :cve_nist:`2025-21718`, :cve_nist:`2025-21726`,
   :cve_nist:`2025-21727`, :cve_nist:`2025-21731`, :cve_nist:`2025-21735`, :cve_nist:`2025-21736`,
   :cve_nist:`2025-21741`, :cve_nist:`2025-21742`, :cve_nist:`2025-21743`, :cve_nist:`2025-21744`,
   :cve_nist:`2025-21745`, :cve_nist:`2025-21748`, :cve_nist:`2025-21749`, :cve_nist:`2025-21753`,
   :cve_nist:`2025-21756`, :cve_nist:`2025-21759`, :cve_nist:`2025-21760`, :cve_nist:`2025-21761`,
   :cve_nist:`2025-21762`, :cve_nist:`2025-21763`, :cve_nist:`2025-21764`, :cve_nist:`2025-21773`,
   :cve_nist:`2025-21775`, :cve_nist:`2025-21776`, :cve_nist:`2025-21779`, :cve_nist:`2025-21780`,
   :cve_nist:`2025-21782`, :cve_nist:`2025-21783`, :cve_nist:`2025-21785`, :cve_nist:`2025-21787`,
   :cve_nist:`2025-21789`, :cve_nist:`2025-21790`, :cve_nist:`2025-21791`, :cve_nist:`2025-21792`,
   :cve_nist:`2025-21793`, :cve_nist:`2025-21796`, :cve_nist:`2025-21811`, :cve_nist:`2025-21812`,
   :cve_nist:`2025-21814`, :cve_nist:`2025-21820`, :cve_nist:`2025-21844`, :cve_nist:`2025-21846`,
   :cve_nist:`2025-21847`, :cve_nist:`2025-21848`, :cve_nist:`2025-21853`, :cve_nist:`2025-21854`,
   :cve_nist:`2025-21855`, :cve_nist:`2025-21856`, :cve_nist:`2025-21857`, :cve_nist:`2025-21858`,
   :cve_nist:`2025-21859`, :cve_nist:`2025-21862`, :cve_nist:`2025-21863`, :cve_nist:`2025-21864`,
   :cve_nist:`2025-21865`, :cve_nist:`2025-21866`, :cve_nist:`2025-21867`, :cve_nist:`2025-21887`,
   :cve_nist:`2025-21891`, :cve_nist:`2025-21898`, :cve_nist:`2025-21904`, :cve_nist:`2025-21905`,
   :cve_nist:`2025-21908`, :cve_nist:`2025-21912`, :cve_nist:`2025-21915`, :cve_nist:`2025-21917`,
   :cve_nist:`2025-21918`, :cve_nist:`2025-21919`, :cve_nist:`2025-21920`, :cve_nist:`2025-21922`,
   :cve_nist:`2025-21928`, :cve_nist:`2025-21934`, :cve_nist:`2025-21936`, :cve_nist:`2025-21937`,
   :cve_nist:`2025-21941`, :cve_nist:`2025-21943`, :cve_nist:`2025-21945`, :cve_nist:`2025-21947`,
   :cve_nist:`2025-21948`, :cve_nist:`2025-21951`, :cve_nist:`2025-21957`, :cve_nist:`2025-21959`,
   :cve_nist:`2025-21962`, :cve_nist:`2025-21963`, :cve_nist:`2025-21964`, :cve_nist:`2025-21966`,
   :cve_nist:`2025-21967`, :cve_nist:`2025-21968`, :cve_nist:`2025-21969`, :cve_nist:`2025-21979`,
   :cve_nist:`2025-21980`, :cve_nist:`2025-21981`, :cve_nist:`2025-21991` and :cve_nist:`2025-21993`
-  mpg123: Fix :cve_nist:`2024-10573`
-  ofono: Fix :cve_nist:`2024-7537`
-  openssh: Fix :cve_nist:`2025-26465`
-  puzzles: Ignore :cve_nist:`2024-13769`, :cve_nist:`2024-13770` and :cve_nist:`2025-0837`
-  qemu: Ignore :cve_nist:`2023-1386`
-  ruby: Fix :cve_nist:`2025-27219` and :cve_nist:`2025-27220`
-  rust-cross-canadian: Ignore :cve_nist:`2024-43402`
-  vim: Fix :cve_nist:`2025-1215`, :cve_nist:`2025-26603`, :cve_nist:`2025-27423` and
   :cve_nist:`2025-29768`
-  xserver-xorg: Fix :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`,
   :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600`
   and :cve_nist:`2025-26601`
-  xz: Fix :cve_nist:`2025-31115`


Fixes in Yocto-5.0.9
~~~~~~~~~~~~~~~~~~~~

-  babeltrace2: extend to nativesdk
-  babeltrace: extend to nativesdk
-  bitbake: event/utils: Avoid deadlock from lock_timeout() and recursive events
-  bitbake: utils: Add signal blocking for lock_timeout
-  bitbake: utils: Print information about lock issue before exiting
-  bitbake: utils: Tweak lock_timeout logic
-  build-appliance-image: Update to scarthgap head revision
-  cve-check.bbclass: Mitigate symlink related error
-  cve-update-nvd2-native: add workaround for json5 style list
-  cve-update-nvd2-native: handle missing vulnStatus
-  gcc: remove paths to sysroot from configargs.h and checksum-options for gcc-cross-canadian
-  gcc: unify cleanup of include-fixed, apply to cross-canadian
-  ghostscript: upgrade to 10.05.0
-  grub: backport strlcpy function
-  grub: drop obsolete CVE statuses
-  icu: Adjust ICU_DATA_DIR path on big endian targets
-  kernel-arch: add macro-prefix-map in KERNEL_CC
-  libarchive: upgrade to 3.7.9
-  libxslt: upgrade to 1.1.43
-  linux-yocto/6.6: update to v6.6.84
-  mc: set ac_cv_path_ZIP to avoid buildpaths QA issues
-  mpg123: upgrade to 1.32.10
-  nativesdk-libtool: sanitize the script, remove buildpaths
-  openssl: rewrite ptest installation
-  overview-manual/concepts: remove :term:`PR` from the build dir list
-  patch.py: set commituser and commitemail for addNote
-  poky.conf: bump version for 5.0.9
-  vim: Upgrade to 9.1.1198
-  xserver-xf86-config: add a configuration fragment to disable screen blanking
-  xserver-xf86-config: remove obsolete configuration files
-  xserver-xorg: upgrade to 21.1.16
-  xz: upgrade to 5.4.7
-  yocto-uninative: Update to 4.7 for glibc 2.41


Known Issues in Yocto-5.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Antonin Godard
-  Archana Polampalli
-  Ashish Sharma
-  Bruce Ashfield
-  Changqing Li
-  Denys Dmytriyenko
-  Divya Chellam
-  Hitendra Prajapati
-  Madhu Marri
-  Makarios Christakis
-  Martin Jansa
-  Michael Halstead
-  Niko Mauno
-  Oleksandr Hnatiuk
-  Peter Marko
-  Richard Purdie
-  Ross Burton
-  Sana Kazi
-  Stefan Mueller-Klieser
-  Steve Sakoman
-  Vijay Anusuri
-  Virendra Thakur
-  Vishwas Udupa
-  Wang Mingyu
-  Zhang Peng


Repositories / Downloads for Yocto-5.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.9 </poky/log/?h=yocto-5.0.9>`
-  Git Revision: :yocto_git:`bab0f9f62af9af580744948dd3240f648a99879a </poky/commit/?id=bab0f9f62af9af580744948dd3240f648a99879a>`
-  Release Artefact: poky-bab0f9f62af9af580744948dd3240f648a99879a
-  sha: ee6811d9fb6c4913e19d6e3569f1edc8ccd793779b237520596506446a6b4531
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.9/poky-bab0f9f62af9af580744948dd3240f648a99879a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.9/poky-bab0f9f62af9af580744948dd3240f648a99879a.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.9 </openembedded-core/log/?h=yocto-5.0.9>`
-  Git Revision: :oe_git:`04038ecd1edd6592b826665a2b787387bb7074fa </openembedded-core/commit/?id=04038ecd1edd6592b826665a2b787387bb7074fa>`
-  Release Artefact: oecore-04038ecd1edd6592b826665a2b787387bb7074fa
-  sha: 6e201a4b486dfbdfcb7e96d83b962a205ec4764db6ad0e34bd623db18910eddb
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.9/oecore-04038ecd1edd6592b826665a2b787387bb7074fa.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.9/oecore-04038ecd1edd6592b826665a2b787387bb7074fa.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.9 </meta-mingw/log/?h=yocto-5.0.9>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.9/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.9/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.9 </bitbake/log/?h=yocto-5.0.9>`
-  Git Revision: :oe_git:`696c2c1ef095f8b11c7d2eff36fae50f58c62e5e </bitbake/commit/?id=696c2c1ef095f8b11c7d2eff36fae50f58c62e5e>`
-  Release Artefact: bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e
-  sha: fc83f879cd6dd14b9b7eba0161fec23ecc191fed0fb00556ba729dceef6c145f
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.9/bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.9/bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.9 </yocto-docs/log/?h=yocto-5.0.9>`
-  Git Revision: :yocto_git:`56db4fd81f6235428bef9e46a61c11ca0ba89733 </yocto-docs/commit/?id=56db4fd81f6235428bef9e46a61c11ca0ba89733>`

