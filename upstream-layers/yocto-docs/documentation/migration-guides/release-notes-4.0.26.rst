Release notes for Yocto-4.0.26 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.26
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


-  bind: Fix :cve_nist:`2024-11187` and :cve_nist:`2024-12705`
-  binutils: Fix :cve_nist:`2025-0840`
-  elfutils: Fix :cve_nist:`2025-1352` and :cve_nist:`2025-1372`
-  ffmpeg: Fix CVE-2024-28661, :cve_nist:`2024-35369`, :cve_nist:`2024-36613`, :cve_nist:`2024-36616`,
   :cve_nist:`2024-36617`, :cve_nist:`2024-36618`, :cve_nist:`2025-0518` and :cve_nist:`2025-25473`
-  ffmpeg: Ignore :cve_nist:`2023-46407`, :cve_nist:`2023-47470`, :cve_nist:`2024-7272`,
   :cve_nist:`2024-22860`, :cve_nist:`2024-22861` and :cve_nist:`2024-22862`
-  freetype: Fix :cve_nist:`2025-27363`
-  gnutls: Fix :cve_nist:`2024-12243`
-  grub: Fix :cve_nist:`2024-45774`, :cve_nist:`2024-45775`, :cve_nist:`2024-45776`,
   :cve_nist:`2024-45777`, :cve_nist:`2024-45778`, :cve_nist:`2024-45779`, :cve_nist:`2024-45780`,
   :cve_nist:`2024-45781`, :cve_nist:`2024-45782`, :cve_nist:`2024-45783`, :cve_nist:`2024-56737`,
   :cve_nist:`2025-0622`, :cve_nist:`2025-0624`, :cve_nist:`2025-0677`, :cve_nist:`2025-0684`,
   :cve_nist:`2025-0685`, :cve_nist:`2025-0686`, :cve_nist:`2025-0689`, :cve_nist:`2025-0678`,
   :cve_nist:`2025-0690`, :cve_nist:`2025-1118` and :cve_nist:`2025-1125`
-  gstreamer1.0-rtsp-server: fix :cve_nist:`2024-44331`
-  libarchive: Fix :cve_nist:`2025-25724`
-  libarchive: Ignore :cve_nist:`2025-1632`
-  libcap: Fix :cve_nist:`2025-1390`
-  linux-yocto/5.10: Fix :cve_nist:`2024-36476`, :cve_nist:`2024-43098`, :cve_nist:`2024-47143`,
   :cve_nist:`2024-48881`, :cve_nist:`2024-50051`, :cve_nist:`2024-50074`, :cve_nist:`2024-50082`,
   :cve_nist:`2024-50083`, :cve_nist:`2024-50099`, :cve_nist:`2024-50115`, :cve_nist:`2024-50116`,
   :cve_nist:`2024-50117`, :cve_nist:`2024-50142`, :cve_nist:`2024-50148`, :cve_nist:`2024-50150`,
   :cve_nist:`2024-50151`, :cve_nist:`2024-50167`, :cve_nist:`2024-50168`, :cve_nist:`2024-50171`,
   :cve_nist:`2024-50185`, :cve_nist:`2024-50192`, :cve_nist:`2024-50193`, :cve_nist:`2024-50194`,
   :cve_nist:`2024-50195`, :cve_nist:`2024-50198`, :cve_nist:`2024-50201`, :cve_nist:`2024-50202`,
   :cve_nist:`2024-50205`, :cve_nist:`2024-50208`, :cve_nist:`2024-50209`, :cve_nist:`2024-50229`,
   :cve_nist:`2024-50230`, :cve_nist:`2024-50233`, :cve_nist:`2024-50234`, :cve_nist:`2024-50236`,
   :cve_nist:`2024-50237`, :cve_nist:`2024-50251`, :cve_nist:`2024-50262`, :cve_nist:`2024-50264`,
   :cve_nist:`2024-50265`, :cve_nist:`2024-50267`, :cve_nist:`2024-50268`, :cve_nist:`2024-50269`,
   :cve_nist:`2024-50273`, :cve_nist:`2024-50278`, :cve_nist:`2024-50279`, :cve_nist:`2024-50282`,
   :cve_nist:`2024-50287`, :cve_nist:`2024-50292`, :cve_nist:`2024-50296`, :cve_nist:`2024-50299`,
   :cve_nist:`2024-50301`, :cve_nist:`2024-50302`, :cve_nist:`2024-53042`, :cve_nist:`2024-53052`,
   :cve_nist:`2024-53057`, :cve_nist:`2024-53059`, :cve_nist:`2024-53060`, :cve_nist:`2024-53061`,
   :cve_nist:`2024-53063`, :cve_nist:`2024-53066`, :cve_nist:`2024-53096`, :cve_nist:`2024-53097`,
   :cve_nist:`2024-53101`, :cve_nist:`2024-53103`, :cve_nist:`2024-53104`, :cve_nist:`2024-53145`,
   :cve_nist:`2024-53146`, :cve_nist:`2024-53150`, :cve_nist:`2024-53155`, :cve_nist:`2024-53156`,
   :cve_nist:`2024-53157`, :cve_nist:`2024-53161`, :cve_nist:`2024-53165`, :cve_nist:`2024-53171`,
   :cve_nist:`2024-53173`, :cve_nist:`2024-53174`, :cve_nist:`2024-53194`, :cve_nist:`2024-53197`,
   :cve_nist:`2024-53217`, :cve_nist:`2024-53226`, :cve_nist:`2024-53227`, :cve_nist:`2024-53237`,
   :cve_nist:`2024-53239`, :cve_nist:`2024-55916`, :cve_nist:`2024-56548`, :cve_nist:`2024-56558`,
   :cve_nist:`2024-56567`, :cve_nist:`2024-56568`, :cve_nist:`2024-56569`, :cve_nist:`2024-56572`,
   :cve_nist:`2024-56574`, :cve_nist:`2024-56581`, :cve_nist:`2024-56587`, :cve_nist:`2024-56593`,
   :cve_nist:`2024-56595`, :cve_nist:`2024-56596`, :cve_nist:`2024-56598`, :cve_nist:`2024-56600`,
   :cve_nist:`2024-56601`, :cve_nist:`2024-56602`, :cve_nist:`2024-56603`, :cve_nist:`2024-56605`,
   :cve_nist:`2024-56606`, :cve_nist:`2024-56615`, :cve_nist:`2024-56619`, :cve_nist:`2024-56623`,
   :cve_nist:`2024-56629`, :cve_nist:`2024-56634`, :cve_nist:`2024-56642`, :cve_nist:`2024-56643`,
   :cve_nist:`2024-56648`, :cve_nist:`2024-56650`, :cve_nist:`2024-56659`, :cve_nist:`2024-56662`,
   :cve_nist:`2024-56670`, :cve_nist:`2024-56688`, :cve_nist:`2024-56698`, :cve_nist:`2024-56704`,
   :cve_nist:`2024-56716`, :cve_nist:`2024-56720`, :cve_nist:`2024-56723`, :cve_nist:`2024-56724`,
   :cve_nist:`2024-56728`, :cve_nist:`2024-56739`, :cve_nist:`2024-56746`, :cve_nist:`2024-56747`,
   :cve_nist:`2024-56748`, :cve_nist:`2024-56754`, :cve_nist:`2024-56756`, :cve_nist:`2024-56770`,
   :cve_nist:`2024-56779`, :cve_nist:`2024-56780`, :cve_nist:`2024-56781`, :cve_nist:`2024-56785`,
   :cve_nist:`2024-57802`, :cve_nist:`2024-57807`, :cve_nist:`2024-57850`, :cve_nist:`2024-57874`,
   :cve_nist:`2024-57890`, :cve_nist:`2024-57896`, :cve_nist:`2024-57900`, :cve_nist:`2024-57901`,
   :cve_nist:`2024-57902`, :cve_nist:`2024-57910`, :cve_nist:`2024-57911`, :cve_nist:`2024-57913`,
   :cve_nist:`2024-57922`, :cve_nist:`2024-57938`, :cve_nist:`2024-57939`, :cve_nist:`2024-57946`,
   :cve_nist:`2024-57951`, :cve_nist:`2025-21638`, :cve_nist:`2025-21687`, :cve_nist:`2025-21689`,
   :cve_nist:`2025-21692`, :cve_nist:`2025-21694`, :cve_nist:`2025-21697` and :cve_nist:`2025-21699`
-  linux-yocto/5.15: Fix :cve_nist:`2024-57979`, :cve_nist:`2024-58034`, :cve_nist:`2024-58052`,
   :cve_nist:`2024-58055`, :cve_nist:`2024-58058`, :cve_nist:`2024-58063`, :cve_nist:`2024-58069`,
   :cve_nist:`2024-58071`, :cve_nist:`2024-58076`, :cve_nist:`2024-58083`, :cve_nist:`2025-21700`,
   :cve_nist:`2025-21703`, :cve_nist:`2025-21715`, :cve_nist:`2025-21722`, :cve_nist:`2025-21727`,
   :cve_nist:`2025-21731`, :cve_nist:`2025-21753`, :cve_nist:`2025-21756`, :cve_nist:`2025-21760`,
   :cve_nist:`2025-21761`, :cve_nist:`2025-21762`, :cve_nist:`2025-21763`, :cve_nist:`2025-21764`,
   :cve_nist:`2025-21796`, :cve_nist:`2025-21811`, :cve_nist:`2025-21887`, :cve_nist:`2025-21898`,
   :cve_nist:`2025-21904`, :cve_nist:`2025-21905`, :cve_nist:`2025-21912`, :cve_nist:`2025-21917`,
   :cve_nist:`2025-21919`, :cve_nist:`2025-21920`, :cve_nist:`2025-21922`, :cve_nist:`2025-21934`,
   :cve_nist:`2025-21943`, :cve_nist:`2025-21948` and :cve_nist:`2025-21951`
-  libpcre2: Ignore :cve_nist:`2022-1586`
-  libtasn1: Fix :cve_nist:`2024-12133`
-  libxml2: Fix :cve_nist:`2022-49043`, :cve_nist:`2024-56171`, :cve_nist:`2025-24928` and
   :cve_nist:`2025-27113`
-  libxslt: Fix :cve_nist:`2024-55549` and :cve_nist:`2025-24855`
-  llvm: Fix :cve_nist:`2024-0151`
-  mpg123: Fix :cve_nist:`2024-10573`
-  openssh: Fix :cve_nist:`2025-26465`
-  ovmf: Revert Fix for CVE-2023-45236 :cve_nist:`2023-45237`
-  perl: Ignore :cve_nist:`2023-47038`
-  puzzles: Ignore :cve_nist:`2024-13769`, :cve_nist:`2024-13770` and :cve_nist:`2025-0837`
-  python3: Fix :cve_nist:`2025-0938`
-  ruby: Fix :cve_nist:`2024-41946`, :cve_nist:`2025-27219` and :cve_nist:`2025-27220`
-  subversion: Ignore :cve_nist:`2024-45720`
-  systemd: Fix :cve_nist:`2022-3821`, :cve_nist:`2022-4415`, :cve_nist:`2022-45873` and
   :cve_nist:`2023-7008`
-  tiff: mark :cve_nist:`2023-30774` as patched with existing patch
-  u-boot: Fix :cve_nist:`2022-2347`, :cve_nist:`2022-30767`, :cve_nist:`2022-30790`,
   :cve_nist:`2024-57254`, :cve_nist:`2024-57255`, :cve_nist:`2024-57256`, :cve_nist:`2024-57257`,
   :cve_nist:`2024-57258` and :cve_nist:`2024-57259`
-  vim: Fix :cve_nist:`2025-1215`, :cve_nist:`2025-22134`, :cve_nist:`2025-24014`,
   :cve_nist:`2025-26603`, :cve_nist:`2025-27423` and :cve_nist:`2025-29768`
-  xserver-xorg: Fix :cve_nist:`2022-49737`, :cve_nist:`2025-26594`, :cve_nist:`2025-26595`,
   :cve_nist:`2025-26596`, :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`,
   :cve_nist:`2025-26600` and :cve_nist:`2025-26601`
-  xwayland: Fix :cve_nist:`2022-49737`, :cve_nist:`2024-9632`, :cve_nist:`2024-21885`,
   :cve_nist:`2024-21886`, :cve_nist:`2024-31080`, :cve_nist:`2024-31081`, :cve_nist:`2024-31083`,
   :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`, :cve_nist:`2025-26597`,
   :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600` and :cve_nist:`2025-26601`
-  zlib: Fix :cve_nist:`2014-9485`



Fixes in Yocto-4.0.26
~~~~~~~~~~~~~~~~~~~~~

-  bind: Upgrade to 9.18.33
-  bitbake: cache: bump cache version
-  bitbake: siggen.py: Improve taskhash reproducibility
-  boost: fix do_fetch error
-  build-appliance-image: Update to kirkstone head revision
-  contributor-guide/submit-changes: add policy on AI generated code
-  cve-update-nvd2-native: handle missing vulnStatus
-  docs: Add favicon for the documentation html
-  docs: Remove all mention of core-image-lsb
-  libtasn1: upgrade to 4.20.0
-  libxcrypt-compat: Remove libcrypt.so to fix conflict with libcrypt
-  libxml2: fix compilation of explicit child axis in pattern
-  linux-yocto/5.10: update to v5.10.234
-  linux-yocto/5.15: update to v5.15.179
-  mesa: Fix missing GLES3 headers in SDK sysroot
-  mesa: Update :term:`SRC_URI`
-  meta: Enable '-o pipefail' for the SDK installer
-  migration-guides: add release notes for 4.0.25
-  poky.conf: add ubuntu2404 to :term:`SANITY_TESTED_DISTROS`
-  poky.conf: bump version for 4.0.26
-  procps: replaced one use of fputs(3) with a write(2) call
-  ref-manual: don't refer to poky-lsb
-  scripts/install-buildtools: Update to 4.0.24
-  scritps/runqemu: Ensure we only have two serial ports
-  systemd: upgrade to 250.14
-  tzcode-native: Fix compiler setting from 2023d version
-  tzcode: Update :term:`SRC_URI`
-  tzdata/tzcode-native: upgrade 2025a
-  vim: Upgrade to 9.1.1198
-  virglrenderer: fix do_fetch error
-  vulnerabilities/classes: remove references to cve-check text format
-  xz: Update :term:`SRC_URI`
-  yocto-uninative: Update to 4.7 for glibc 2.41


Known Issues in Yocto-4.0.26
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.26
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Aleksandar Nikolic
-  Alessio Cascone
-  Antonin Godard
-  Archana Polampalli
-  Ashish Sharma
-  Bruce Ashfield
-  Carlos Dominguez
-  Deepesh Varatharajan
-  Divya Chellam
-  Guocai He
-  Hitendra Prajapati
-  Hongxu Jia
-  Jiaying Song
-  Johannes Kauffmann
-  Kai Kang
-  Lee Chee Yang
-  Libo Chen
-  Marta Rybczynska
-  Michael Halstead
-  Mingli Yu
-  Moritz Haase
-  Narpat Mali
-  Paulo Neves
-  Peter Marko
-  Priyal Doshi
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Sakib Sajal
-  Steve Sakoman
-  Vijay Anusuri
-  Yogita Urade
-  Zhang Peng


Repositories / Downloads for Yocto-4.0.26
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.26 </poky/log/?h=yocto-4.0.26>`
-  Git Revision: :yocto_git:`d70d287a77d5026b698ac237ab865b2dafd36bb8 </poky/commit/?id=d70d287a77d5026b698ac237ab865b2dafd36bb8>`
-  Release Artefact: poky-d70d287a77d5026b698ac237ab865b2dafd36bb8
-  sha: 3ebfadb8bff4c1ca12b3cf3e4ef6e3ac2ce52b73570266daa98436c9959249f2
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.26/poky-d70d287a77d5026b698ac237ab865b2dafd36bb8.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.26/poky-d70d287a77d5026b698ac237ab865b2dafd36bb8.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.26 </openembedded-core/log/?h=yocto-4.0.26>`
-  Git Revision: :oe_git:`1efbe1004bc82e7c14c1e8bd4ce644f5015c3346 </openembedded-core/commit/?id=1efbe1004bc82e7c14c1e8bd4ce644f5015c3346>`
-  Release Artefact: oecore-1efbe1004bc82e7c14c1e8bd4ce644f5015c3346
-  sha: d3805e034dabd0865dbf55488b2c16d4ea0351d37aa826f0054a6bfdde5a8be9
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.26/oecore-1efbe1004bc82e7c14c1e8bd4ce644f5015c3346.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.26/oecore-1efbe1004bc82e7c14c1e8bd4ce644f5015c3346.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.26 </meta-mingw/log/?h=yocto-4.0.26>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.26/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.26/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.26 </meta-gplv2/log/?h=yocto-4.0.26>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.26/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.26/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.26 </bitbake/log/?h=yocto-4.0.26>`
-  Git Revision: :oe_git:`046871d9fd76efdca7b72718b328d8f545523f7e </bitbake/commit/?id=046871d9fd76efdca7b72718b328d8f545523f7e>`
-  Release Artefact: bitbake-046871d9fd76efdca7b72718b328d8f545523f7e
-  sha: e9df0a9f5921b583b539188d66b23f120e1751000e7822e76c3391d5c76ee21a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.26/bitbake-046871d9fd76efdca7b72718b328d8f545523f7e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.26/bitbake-046871d9fd76efdca7b72718b328d8f545523f7e.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.26 </yocto-docs/log/?h=yocto-4.0.26>`
-  Git Revision: :yocto_git:`9b4c36f7b02dd4bedfec90206744a1e90e37733c </yocto-docs/commit/?id=9b4c36f7b02dd4bedfec90206744a1e90e37733c>`

