.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.1 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Fixes in Yocto-5.0.1
~~~~~~~~~~~~~~~~~~~~

-  babeltrace2: upgrade 2.0.5 -> 2.0.6
-  bind: upgrade 9.18.24 -> 9.18.25
-  bitbake: cooker: Use hash client to ping upstream server
-  build-appliance-image: Update to scarthgap head revision (b9b47b1a392b...)
-  docs: add support for scarthgap 5.0 release
-  docs: brief-yoctoprojectqs: explicit version dependency on websockets python module
-  docs: brief-yoctoprojectqs: Update to the correct hash equivalence server address
-  documentation/poky.yaml.in: drop mesa/sdl from essential host packages
-  ell: upgrade 0.63 -> 0.64
-  gcr: upgrade 4.2.0 -> 4.2.1
-  icu: update 74-1 -> 74-2
-  libdnf: upgrade 0.73.0 -> 0.73.1
-  libsdl2: upgrade 2.30.0 -> 2.30.1
-  libx11: upgrade 1.8.7 -> 1.8.9
-  libxcursor: upgrade 1.2.1 -> 1.2.2
-  libxml2: upgrade 2.12.5 -> 2.12.6
-  local.conf.sample: Fix hashequivalence server address
-  lttng-tools: upgrade 2.13.11 -> 2.13.13
-  manuals: standards.md: add standard for project names
-  mesa: upgrade 24.0.2 -> 24.0.3
-  migration-notes: add release notes for 4.0.18
-  mpg123: upgrade 1.32.5 -> 1.32.6
-  pango: upgrade 1.52.0 -> 1.52.1
-  poky.conf: bump version for 5.0.1
-  python3: skip test_concurrent_futures/test_shutdown
-  ref-manual: update releases.svg
-  ref-manual: variables: add :term:`USERADD_DEPENDS`
-  release-notes-5.0: update Repositories / Downloads section
-  release-notes-5.0: update recipes changes
-  release-notes-5.0: update new features
-  rootfs-postcommands.bbclass: Only set DROPBEAR_RSAKEY_DIR once
-  rpm: update 4.19.1 -> 4.19.1.1
-  scripts/oe-setup-build: write a build environment initialization one-liner into the build directory
-  sstate.bbclass: Add _SSTATE_EXCLUDEDEPS_SYSROOT to vardepsexclude
-  systemd: sed :term:`ROOT_HOME` only if sysusers :term:`PACKAGECONFIG` is set


Known Issues in Yocto-5.0.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.0.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Christian Bräuner Sørensen
-  Joshua Watt
-  Lee Chee Yang
-  Mark Hatle
-  Michael Glembotzki
-  Michael Halstead
-  Michael Opdenacker
-  Paul Eggleton
-  Quentin Schulz
-  Richard Purdie
-  Steve Sakoman
-  Trevor Gamblin
-  Wang Mingyu


Repositories / Downloads for Yocto-5.0.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.1 </poky/log/?h=yocto-5.0.1>`
-  Git Revision: :yocto_git:`4b07a5316ed4b858863dfdb7cab63859d46d1810 </poky/commit/?id=4b07a5316ed4b858863dfdb7cab63859d46d1810>`
-  Release Artefact: poky-4b07a5316ed4b858863dfdb7cab63859d46d1810
-  sha: 51d0c84da7dbcc8db04a674da39cfc73ea78aac22ee646ede5b6229937d4666a
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.1/poky-4b07a5316ed4b858863dfdb7cab63859d46d1810.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.1/poky-4b07a5316ed4b858863dfdb7cab63859d46d1810.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.1 </openembedded-core/log/?h=yocto-5.0.1>`
-  Git Revision: :oe_git:`294a7dbe44f6b7c8d3a1de8c2cc182af37c4f916 </openembedded-core/commit/?id=294a7dbe44f6b7c8d3a1de8c2cc182af37c4f916>`
-  Release Artefact: oecore-294a7dbe44f6b7c8d3a1de8c2cc182af37c4f916
-  sha: e9be51a3b1fe8a1f420483b912caf91bc429dcca303d462381876a643b73045e
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.1/oecore-294a7dbe44f6b7c8d3a1de8c2cc182af37c4f916.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.1/oecore-294a7dbe44f6b7c8d3a1de8c2cc182af37c4f916.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.1 </meta-mingw/log/?h=yocto-5.0.1>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.1/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.1/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.1 </bitbake/log/?h=yocto-5.0.1>`
-  Git Revision: :oe_git:`8f90d10f9efc9a32e13f6bd031992aece79fe7cc </bitbake/commit/?id=8f90d10f9efc9a32e13f6bd031992aece79fe7cc>`
-  Release Artefact: bitbake-8f90d10f9efc9a32e13f6bd031992aece79fe7cc
-  sha: 519f02d5de7fbfac411532161d521123814dd9cc7d6b55488b5e7a547c1a6977
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.1/bitbake-8f90d10f9efc9a32e13f6bd031992aece79fe7cc.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.1/bitbake-8f90d10f9efc9a32e13f6bd031992aece79fe7cc.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.1 </yocto-docs/log/?h=yocto-5.0.1>`
-  Git Revision: :yocto_git:`875dfe69e93bf8fee3b8c07818a6ac059f228a13 </yocto-docs/commit/?id=875dfe69e93bf8fee3b8c07818a6ac059f228a13>`

