.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.3.3 (Nanbield)
----------------------------------------

Security Fixes in Yocto-4.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  curl: Fix :cve_nist:`2023-46219`
-  glibc: Ignore fixed :cve_nist:`2023-0687` and :cve_nist:`2023-5156`
-  linux-yocto/6.1: Ignore :cve_nist:`2022-48619`, :cve_nist:`2023-4610`, :cve_nist:`2023-5178`, :cve_nist:`2023-5972`, :cve_nist:`2023-6040`, :cve_nist:`2023-6531`, :cve_nist:`2023-6546`, :cve_nist:`2023-6622`, :cve_nist:`2023-6679`, :cve_nist:`2023-6817`, :cve_nist:`2023-6931`, :cve_nist:`2023-6932`, :cve_nist:`2023-7192`, :cve_nist:`2024-0193` and :cve_nist:`2024-0443`
-  linux-yocto/6.1: Fix :cve_nist:`2023-1193`, :cve_mitre:`2023-51779`, :cve_nist:`2023-51780`, :cve_nist:`2023-51781`, :cve_nist:`2023-51782` and :cve_nist:`2023-6606`
-  qemu: Fix :cve_nist:`2023-3019`
-  shadow: Fix :cve_nist:`2023-4641`
-  sqlite3: Fix :cve_nist:`2024-0232`
-  sqlite3: drop obsolete CVE ignore :cve_nist:`2023-36191`
-  sudo: Fix :cve_nist:`2023-42456` and :cve_nist:`2023-42465`
-  tiff: Fix :cve_nist:`2023-6277`
-  xwayland: Fix :cve_nist:`2023-6377` and :cve_nist:`2023-6478`


Fixes in Yocto-4.3.3
~~~~~~~~~~~~~~~~~~~~

-  aspell: upgrade to 0.60.8.1
-  avahi: update URL for new project location
-  base-passwd: upgrade to 3.6.3
-  bitbake: asyncrpc: Add context manager API
-  bitbake: toaster/toastergui: Bug-fix verify given layer path only if import/add local layer
-  build-appliance-image: Update to nanbield head revision
-  classes-global/sstate: Fix variable typo
-  cmake: Unset CMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES
-  contributor-guide: fix lore URL
-  contributor-guide: use "apt" instead of "aptitude"
-  create-spdx-2.2: combine spdx can try to write before dir creation
-  curl: Disable test 1091 due to intermittent failures
-  curl: Disable two intermittently failing tests
-  dev-manual: gen-tapdevs need iptables installed
-  dev-manual: start.rst: Update use of Download page
-  dev-manual: update license manifest path
-  devtool: deploy: provide max_process to strip_execs
-  devtool: modify: Handle recipes with a menuconfig task correctly
-  docs: document VSCode extension
-  dtc: preserve version also from shallow git clones
-  elfutils: Update license information
-  glib-2.0: upgrade to 2.78.3
-  glibc-y2038-tests: do not run tests using 32 bit time APIs
-  go: upgrade to 1.20.12
-  grub: fs/fat: Don't error when mtime is 0
-  gstreamer1.0: upgrade to 1.22.8
-  icon-naming-utils: take tarball from debian
-  kea: upgrade to 2.4.1
-  lib/prservice: Improve lock handling robustness
-  libadwaita: upgrade to 1.4.2
-  libatomic-ops: upgrade to 7.8.2
-  libva-utils: upgrade to 2.20.1
-  linux-firmware: Change bnx2 packaging
-  linux-firmware: Create bnx2x subpackage
-  linux-firmware: Fix the linux-firmware-bcm4373 :term:`FILES` variable
-  linux-firmware: Package iwlwifi .pnvm files
-  linux-yocto/6.1: security/cfg: add configs to harden protection
-  linux-yocto/6.1: update to v6.1.73
-  meta/documentation.conf: fix do_menuconfig description
-  migration-guide: add release notes for 4.0.16
-  migration-guide: add release notes for 4.3.2
-  ncurses: Fix - tty is hung after reset
-  nfs-utils: Update Upstream-Status
-  nfs-utils: upgrade to 2.6.4
-  oeqa/selftest/prservice: Improve test robustness
-  package.py: OEHasPackage: Add :term:`MLPREFIX` to packagename
-  poky.conf: bump version for 4.3.3 release
-  pseudo: Update to pull in syncfs probe fix
-  python3-license-expression: Fix the ptest failure
-  qemu.bbclass: fix a python TypeError
-  qemu: upgrade to 8.1.4
-  ref-manual: Add UBOOT_BINARY, extend :term:`UBOOT_CONFIG`
-  ref-manual: classes: remove insserv bbclass
-  ref-manual: update tested and supported distros
-  release-notes-4.3: fix spacing
-  rootfs.py: check depmodwrapper execution result
-  rpcbind: Specify state directory under /run
-  scripts/runqemu: fix regex escape sequences
-  sqlite3: upgrade to 3.43.2
-  sstate: Fix dir ownership issues in :term:`SSTATE_DIR`
-  sudo: upgrade to 1.9.15p5
-  tcl: Fix prepending to run-ptest script
-  uninative-tarball.xz - reproducibility fix
-  xwayland: upgrade to 23.2.3
-  zstd: fix :term:`LICENSE` statement


Known Issues in Yocto-4.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alassane Yattara
-  Alexander Kanavin
-  Anuj Mittal
-  Baruch Siach
-  Bruce Ashfield
-  Chen Qi
-  Clay Chang
-  Enguerrand de Ribaucourt
-  Ilya A. Kriveshko
-  Jason Andryuk
-  Jeremy A. Puhlman
-  Joao Marcos Costa
-  Jose Quaresma
-  Joshua Watt
-  Jörg Sommer
-  Khem Raj
-  Lee Chee Yang
-  Markus Volk
-  Massimiliano Minella
-  Maxin B. John
-  Michael Opdenacker
-  Ming Liu
-  Mingli Yu
-  Peter Kjellerstedt
-  Peter Marko
-  Richard Purdie
-  Robert Berger
-  Robert Yang
-  Rodrigo M. Duarte
-  Ross Burton
-  Saul Wold
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Trevor Gamblin
-  Wang Mingyu
-  William Lyu
-  Xiangyu Chen
-  Yang Xu
-  Zahir Hussain


Repositories / Downloads for Yocto-4.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`nanbield </poky/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.3 </poky/log/?h=yocto-4.3.3>`
-  Git Revision: :yocto_git:`d3b27346c3a4a7ef7ec517e9d339d22bda74349d </poky/commit/?id=d3b27346c3a4a7ef7ec517e9d339d22bda74349d>`
-  Release Artefact: poky-d3b27346c3a4a7ef7ec517e9d339d22bda74349d
-  sha: 2db39f1bf7bbcee039e9970eed1f6f9233bcc95d675159647c9a2a334fc81eb0
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.3/poky-d3b27346c3a4a7ef7ec517e9d339d22bda74349d.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.3/poky-d3b27346c3a4a7ef7ec517e9d339d22bda74349d.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`nanbield </openembedded-core/log/?h=nanbield>`
-  Tag:  :oe_git:`yocto-4.3.3 </openembedded-core/log/?h=yocto-4.3.3>`
-  Git Revision: :oe_git:`0584d01f623e1f9b0fef4dfa95dd66de6cbfb7b3 </openembedded-core/commit/?id=0584d01f623e1f9b0fef4dfa95dd66de6cbfb7b3>`
-  Release Artefact: oecore-0584d01f623e1f9b0fef4dfa95dd66de6cbfb7b3
-  sha: 730de0d5744f139322402ff9a6b2483c6ab929f704cec06258ae51de1daebe3d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.3/oecore-0584d01f623e1f9b0fef4dfa95dd66de6cbfb7b3.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.3/oecore-0584d01f623e1f9b0fef4dfa95dd66de6cbfb7b3.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`nanbield </meta-mingw/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.3 </meta-mingw/log/?h=yocto-4.3.3>`
-  Git Revision: :yocto_git:`49617a253e09baabbf0355bc736122e9549c8ab2 </meta-mingw/commit/?id=49617a253e09baabbf0355bc736122e9549c8ab2>`
-  Release Artefact: meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2
-  sha: 2225115b73589cdbf1e491115221035c6a61679a92a93b2a3cf761ff87bf4ecc
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.3/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.3/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.6 </bitbake/log/?h=2.6>`
-  Tag:  :oe_git:`yocto-4.3.3 </bitbake/log/?h=yocto-4.3.3>`
-  Git Revision: :oe_git:`380a9ac97de5774378ded5e37d40b79b96761a0c </bitbake/commit/?id=380a9ac97de5774378ded5e37d40b79b96761a0c>`
-  Release Artefact: bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c
-  sha: 78f579b9d29e72d09b6fb10ac62aa925104335e92d2afb3155bc9ab1994e36c1
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.3/bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.3/bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`nanbield </yocto-docs/log/?h=nanbield>`
-  Tag: :yocto_git:`yocto-4.3.3 </yocto-docs/log/?h=yocto-4.3.3>`
-  Git Revision: :yocto_git:`dde4b815db82196af086847f68ee27d7902b4ffa </yocto-docs/commit/?id=dde4b815db82196af086847f68ee27d7902b4ffa>`

