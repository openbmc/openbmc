.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.15 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.15
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2023-1981`, :cve_nist:`2023-38469`, :cve_nist:`2023-38470`, :cve_nist:`2023-38471`, :cve_nist:`2023-38472` and :cve_nist:`2023-38473`
-  binutils: Fix :cve_nist:`2022-47007`, :cve_nist:`2022-47010` and :cve_nist:`2022-48064`
-  bluez5: Fix :cve_nist:`2023-45866`
-  ghostscript: Ignore GhostPCL :cve_nist:`2023-38560`
-  gnutls: Fix :cve_nist:`2023-5981`
-  go: Ignore :cve_nist:`2023-45283` and :cve_nist:`2023-45284`
-  grub: Fix :cve_nist:`2023-4692` and :cve_nist:`2023-4693`
-  gstreamer1.0-plugins-bad: Fix :cve_mitre:`2023-44429`
-  libsndfile: Fix :cve_nist:`2022-33065`
-  libwebp: Fix :cve_nist:`2023-4863`
-  openssl: Fix :cve_nist:`2023-5678`
-  python3-cryptography: Fix :cve_nist:`2023-49083`
-  qemu: Fix :cve_nist:`2023-1544`
-  sudo: :cve_nist:`2023-42456` and :cve_mitre:`2023-42465`
-  tiff: Fix :cve_nist:`2023-41175`
-  vim: Fix :cve_nist:`2023-46246`, :cve_nist:`2023-48231`, :cve_nist:`2023-48232`, :cve_nist:`2023-48233`, :cve_nist:`2023-48234`, :cve_nist:`2023-48235`, :cve_nist:`2023-48236`, :cve_nist:`2023-48237` and :cve_nist:`2023-48706`
-  xserver-xorg: Fix :cve_nist:`2023-5367` and :cve_nist:`2023-5380`
-  xwayland: Fix :cve_nist:`2023-5367`


Fixes in Yocto-4.0.15
~~~~~~~~~~~~~~~~~~~~~

-  bash: changes to SIGINT handler while waiting for a child
-  bitbake: Fix disk space monitoring on cephfs
-  bitbake: bitbake-getvar: Make --quiet work with --recipe
-  bitbake: runqueue.py: fix PSI check logic
-  bitbake: runqueue: Add pressure change logging
-  bitbake: runqueue: convert deferral messages from bb.note to bb.debug
-  bitbake: runqueue: fix PSI check calculation
-  bitbake: runqueue: show more pressure data
-  bitbake: runqueue: show number of currently running bitbake threads when pressure changes
-  bitbake: tinfoil: Do not fail when logging is disabled and full config is used
-  build-appliance-image: Update to kirkstone head revision
-  cve-check: don't warn if a patch is remote
-  cve-check: slightly more verbose warning when adding the same package twice
-  cve-check: sort the package list in the JSON report
-  cve-exclusion_5.10.inc: update for 5.10.202
-  go: Fix issue in DNS resolver
-  goarch: Move Go architecture mapping to a library
-  gstreamer1.0-plugins-base: enable glx/opengl support
-  linux-yocto/5.10: update to v5.10.202
-  manuals: update class references
-  migration-guide: add release notes for 4.0.14
-  native: Clear TUNE_FEATURES/ABIEXTENSION
-  openssh: drop sudo from ptest dependencies
-  overview-manual: concepts: Add Bitbake Tasks Map
-  poky.conf: bump version for 4.0.15
-  python3-jinja2: Fixed ptest result output as per the standard
-  ref-manual: classes: explain cml1 class name
-  ref-manual: update :term:`SDK_NAME` variable documentation
-  ref-manual: variables: add :term:`RECIPE_MAINTAINER`
-  ref-manual: variables: document OEQA_REPRODUCIBLE_* variables
-  ref-manual: variables: mention new CDN for :term:`SSTATE_MIRRORS`
-  rust-common: Set llvm-target correctly for cross SDK targets
-  rust-cross-canadian: Fix ordering of target json config generation
-  rust-cross/rust-common: Merge arm target handling code to fix cross-canadian
-  rust-cross: Simplfy the rust_gen_target calls
-  rust-llvm: Allow overriding LLVM target archs
-  sdk-manual: extensible.rst: remove instructions for using SDK functionality directly in a yocto build
-  sudo: upgrade to 1.9.15p2
-  systemtap_git: fix used uninitialized error
-  vim: Improve locale handling
-  vim: Upgrade to 9.0.2130
-  vim: use upstream generated .po files


Known Issues in Yocto-4.0.15
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.15
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Archana Polampalli
-  BELHADJ SALEM Talel
-  Bruce Ashfield
-  Chaitanya Vadrevu
-  Chen Qi
-  Deepthi Hemraj
-  Denys Dmytriyenko
-  Hitendra Prajapati
-  Lee Chee Yang
-  Li Wang
-  Martin Jansa
-  Meenali Gupta
-  Michael Opdenacker
-  Mikko Rapeli
-  Narpat Mali
-  Niko Mauno
-  Ninad Palsule
-  Niranjan Pradhan
-  Paul Eggleton
-  Peter Kjellerstedt
-  Peter Marko
-  Richard Purdie
-  Ross Burton
-  Samantha Jalabert
-  Sanjana
-  Soumya Sambu
-  Steve Sakoman
-  Tim Orling
-  Vijay Anusuri
-  Vivek Kumbhar
-  Wenlin Kang
-  Yogita Urade


Repositories / Downloads for Yocto-4.0.15
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.15 </poky/log/?h=yocto-4.0.15>`
-  Git Revision: :yocto_git:`755632c2fcab43aa05cdcfa529727064b045073c </poky/commit/?id=755632c2fcab43aa05cdcfa529727064b045073c>`
-  Release Artefact: poky-755632c2fcab43aa05cdcfa529727064b045073c
-  sha: b40b43bd270d21a420c399981f9cfe0eb999f15e051fc2c89d124f249cdc0bd5
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.15/poky-755632c2fcab43aa05cdcfa529727064b045073c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.15/poky-755632c2fcab43aa05cdcfa529727064b045073c.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.15 </openembedded-core/log/?h=yocto-4.0.15>`
-  Git Revision: :oe_git:`eea685e1caafd8e8121006d3f8b5d0b8a4f2a933 </openembedded-core/commit/?id=eea685e1caafd8e8121006d3f8b5d0b8a4f2a933>`
-  Release Artefact: oecore-eea685e1caafd8e8121006d3f8b5d0b8a4f2a933
-  sha: ddc3d4a2c8a097f2aa7132ae716affacc44b119c616a1eeffb7db56caa7fc79e
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.15/oecore-eea685e1caafd8e8121006d3f8b5d0b8a4f2a933.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.15/oecore-eea685e1caafd8e8121006d3f8b5d0b8a4f2a933.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.15 </meta-mingw/log/?h=yocto-4.0.15>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.15/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.15/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.15 </meta-gplv2/log/?h=yocto-4.0.15>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.15/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.15/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.15 </bitbake/log/?h=yocto-4.0.15>`
-  Git Revision: :oe_git:`42a1c9fe698a03feb34c5bba223c6e6e0350925b </bitbake/commit/?id=42a1c9fe698a03feb34c5bba223c6e6e0350925b>`
-  Release Artefact: bitbake-42a1c9fe698a03feb34c5bba223c6e6e0350925b
-  sha: 64c684ccd661fa13e25c859dfc68d66bec79281da0f4f81b0d6a9995acb659b5
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.15/bitbake-42a1c9fe698a03feb34c5bba223c6e6e0350925b.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.15/bitbake-42a1c9fe698a03feb34c5bba223c6e6e0350925b.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.15 </yocto-docs/log/?h=yocto-4.0.15>`
-  Git Revision: :yocto_git:`08fda7a5601393617b1ecfe89229459e14a90b1d </yocto-docs/commit/?id=08fda7a5601393617b1ecfe89229459e14a90b1d>`

