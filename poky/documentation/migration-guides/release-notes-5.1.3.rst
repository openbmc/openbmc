Release notes for Yocto-5.1.3 (Styhead)
---------------------------------------

Security Fixes in Yocto-5.1.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  go: Fix :cve_nist:`2024-45336`, :cve_nist:`2024-45341` and :cve_nist:`2025-22866`
-  linux-yocto/6.6: Fix :cve_nist:`2024-36476`, :cve_nist:`2024-53179`, :cve_nist:`2024-56582`,
   :cve_nist:`2024-56703`, :cve_nist:`2024-57801`, :cve_nist:`2024-57802`, :cve_nist:`2024-57841`,
   :cve_nist:`2024-57882`, :cve_nist:`2024-57887`, :cve_nist:`2024-57890`, :cve_nist:`2024-57892`,
   :cve_nist:`2024-57895`, :cve_nist:`2024-57896`, :cve_nist:`2024-57900`, :cve_nist:`2024-57901`,
   :cve_nist:`2024-57902`, :cve_nist:`2024-57906`, :cve_nist:`2024-57907`, :cve_nist:`2024-57908`,
   :cve_nist:`2024-57910`, :cve_nist:`2024-57911`, :cve_nist:`2024-57912`, :cve_nist:`2024-57913`,
   :cve_nist:`2024-57916`, :cve_nist:`2024-57922`, :cve_nist:`2024-57925`, :cve_nist:`2024-57926`,
   :cve_nist:`2024-57933`, :cve_nist:`2024-57938`, :cve_nist:`2024-57939`, :cve_nist:`2024-57940`,
   :cve_nist:`2024-57949`, :cve_nist:`2024-57951`, :cve_nist:`2025-21631`, :cve_nist:`2025-21636`,
   :cve_nist:`2025-21637`, :cve_nist:`2025-21638`, :cve_nist:`2025-21639`, :cve_nist:`2025-21640`,
   :cve_nist:`2025-21642`, :cve_nist:`2025-21652`, :cve_nist:`2025-21658`, :cve_nist:`2025-21665`,
   :cve_nist:`2025-21666`, :cve_nist:`2025-21667`, :cve_nist:`2025-21669`, :cve_nist:`2025-21670`,
   :cve_nist:`2025-21671`, :cve_nist:`2025-21673`, :cve_nist:`2025-21674`, :cve_nist:`2025-21675`,
   :cve_nist:`2025-21676`, :cve_nist:`2025-21680`, :cve_nist:`2025-21681`, :cve_nist:`2025-21683`,
   :cve_nist:`2025-21684`, :cve_nist:`2025-21687`, :cve_nist:`2025-21689`, :cve_nist:`2025-21690`,
   :cve_nist:`2025-21692`, :cve_nist:`2025-21694`, :cve_nist:`2025-21697` and :cve_nist:`2025-21699`
-  pyhton3: Fix CVE-2024-12254, :cve_nist:`2025-0938` and 3 other vulnerabilities (gh-80222, gh-119511
   and gh-126108).
-  socat: Fix :cve_nist:`2024-54661`
-  vim: Fix :cve_nist:`2025-22134` and :cve_nist:`2025-24014`


Fixes in Yocto-5.1.3
~~~~~~~~~~~~~~~~~~~~

-  bitbake: bblayers/query: Fix using "removeprefix" string method
-  bitbake: cooker: Make cooker 'skiplist' per-multiconfig/mc
-  bitbake: tests/fetch: Fix git shallow test failure with git >= 2.48
-  boost: fix do_fetch error
-  build-appliance-image: Update to styhead head revision
-  classes/nativesdk: also override :term:`TUNE_PKGARCH`
-  classes/qemu: use tune to select QEMU_EXTRAOPTIONS, not package architecture
-  cmake: apply parallel build settings to ptest tasks
-  contributor-guide/submit-changes: add policy on AI generated code
-  cve-check: fix cvesInRecord
-  cve-check: restore :term:`CVE_CHECK_SHOW_WARNINGS` functionality
-  dev-manual/building: document the initramfs-framework recipe
-  devtool: ide-sdk recommend :term:`DEBUG_BUILD`
-  devtool: ide-sdk remove the plugin from eSDK installer
-  devtool: ide-sdk sort cmake preset
-  devtool: modify support debug-builds
-  docs: Add favicon for the documentation html
-  docs: Fix typo in standards.md
-  docs: Update autobuilder URLs to valkyrie
-  enchant2: correct :term:`SRC_URI` and other uris
-  go: upgrade to 1.22.12
-  libnsl2: set :term:`CVE_PRODUCT`
-  libxml-parser-perl: correct :term:`SRC_URI`
-  linux-yocto/6.6: update to v6.6.75
-  linux: Modify kernel configuration to fix runqlat issue
-  lrzsz: update :term:`SRC_URI` to avoid redirect
-  migration-guides: add release notes for 4.0.24, 5.0.6, 5.0.7 and 5.1.2
-  oe-selftest: devtool ide-sdk use modify debug-build
-  oeqa/gitarchive: Fix syntax warning
-  poky.conf: bump version for 5.1.3
-  python3: upgrade 3.12.9
-  ref-manual/faq: add q&a on systemd as default
-  resulttool/store: Fix permissions of logarchive
-  rust-target-config: Fix TARGET_C_INT_WIDTH with correct size
-  scripts/install-buildtools: Update to 5.1.2
-  sdk-manual: extensible.rst: devtool ide-sdk improve
-  sdk-manual: extensible.rst: update devtool ide-sdk
-  systemd: set :term:`CVE_PRODUCT`
-  test-manual/ptest: link to common framework ptest classes
-  test-manual/reproducible-builds: fix reproducible links
-  vim: Upgrade 9.1.1043


Known Issues in Yocto-5.1.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.1.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adrian Freihofer
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Bruce Ashfield
-  Chris Laplante
-  Divya Chellam
-  Harish Sadineni
-  Jiaying Song
-  Joerg Schmidt
-  Lee Chee Yang
-  Mikko Rapeli
-  Peter Marko
-  Richard Purdie
-  Ross Burton
-  Simon A. Eugster
-  Steve Sakoman

Repositories / Downloads for Yocto-5.1.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`styhead </poky/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.3 </poky/log/?h=yocto-5.1.3>`
-  Git Revision: :yocto_git:`11a8dec6e29ac0b2fd942c0fc00dd7fc30658841 </poky/commit/?id=11a8dec6e29ac0b2fd942c0fc00dd7fc30658841>`
-  Release Artefact: poky-11a8dec6e29ac0b2fd942c0fc00dd7fc30658841
-  sha: 9ebcacaab53058fd97b06134e06b5883df3c7ddb25dae43a2f3809c4f65d24b5
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.3/poky-11a8dec6e29ac0b2fd942c0fc00dd7fc30658841.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.3/poky-11a8dec6e29ac0b2fd942c0fc00dd7fc30658841.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`styhead </openembedded-core/log/?h=styhead>`
-  Tag:  :oe_git:`yocto-5.1.3 </openembedded-core/log/?h=yocto-5.1.3>`
-  Git Revision: :oe_git:`35ffa0ed523ba95f069dff5b7df3f819ef031015 </openembedded-core/commit/?id=35ffa0ed523ba95f069dff5b7df3f819ef031015>`
-  Release Artefact: oecore-35ffa0ed523ba95f069dff5b7df3f819ef031015
-  sha: 67efedf0afa9ac9e4664f02923a4c5c2429f2f1be697e39f9cbffb9e3f2d9d2c
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.3/oecore-35ffa0ed523ba95f069dff5b7df3f819ef031015.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.3/oecore-35ffa0ed523ba95f069dff5b7df3f819ef031015.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`styhead </meta-mingw/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.3 </meta-mingw/log/?h=yocto-5.1.3>`
-  Git Revision: :yocto_git:`77fe18d4f8ec34501045c5d92ce7e13b1bd129e9 </meta-mingw/commit/?id=77fe18d4f8ec34501045c5d92ce7e13b1bd129e9>`
-  Release Artefact: meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9
-  sha: 4c7f8100a3675d9863e51825def3df5b263ffc81cd57bae26eedbc156d771534
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.3/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.3/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.10 </bitbake/log/?h=2.10>`
-  Tag:  :oe_git:`yocto-5.1.3 </bitbake/log/?h=yocto-5.1.3>`
-  Git Revision: :oe_git:`58e5c70a0572ff5994dc181694e05cd5d3ddaf66 </bitbake/commit/?id=58e5c70a0572ff5994dc181694e05cd5d3ddaf66>`
-  Release Artefact: bitbake-58e5c70a0572ff5994dc181694e05cd5d3ddaf66
-  sha: 8b1d8aa3de6ca8c520f1b528e342e06de0cff918a11d77862c499185a9ba8fec
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.3/bitbake-58e5c70a0572ff5994dc181694e05cd5d3ddaf66.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.3/bitbake-58e5c70a0572ff5994dc181694e05cd5d3ddaf66.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`styhead </yocto-docs/log/?h=styhead>`
-  Tag: :yocto_git:`yocto-5.1.3 </yocto-docs/log/?h=yocto-5.1.3>`
-  Git Revision: :yocto_git:`fd9c744d6e73a5719e61a3c0063b1602ca386e91 </yocto-docs/commit/?id=fd9c744d6e73a5719e61a3c0063b1602ca386e91>`

