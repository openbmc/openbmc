require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-001;apply=yes;striplevel=0;name=patch001 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-002;apply=yes;striplevel=0;name=patch002 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-003;apply=yes;striplevel=0;name=patch003 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-004;apply=yes;striplevel=0;name=patch004 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-005;apply=yes;striplevel=0;name=patch005 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-006;apply=yes;striplevel=0;name=patch006 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-007;apply=yes;striplevel=0;name=patch007 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-008;apply=yes;striplevel=0;name=patch008 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-009;apply=yes;striplevel=0;name=patch009 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-010;apply=yes;striplevel=0;name=patch010 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-011;apply=yes;striplevel=0;name=patch011 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-012;apply=yes;striplevel=0;name=patch012 \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           file://fix-run-builtins.patch \
           file://0001-help-fix-printf-format-security-warning.patch \
           file://bash-memleak-bug-fix-for-builtin-command-read.patch \
           "

SRC_URI[tarball.md5sum] = "148888a7c95ac23705559b6f477dfe25"
SRC_URI[tarball.sha256sum] = "d86b3392c1202e8ff5a423b302e6284db7f8f435ea9f39b5b1b20fd3ac36dfcb"

SRC_URI[patch001.md5sum] = "817d01a6c0af6f79308a8b7b649e53d8"
SRC_URI[patch001.sha256sum] = "3e28d91531752df9a8cb167ad07cc542abaf944de9353fe8c6a535c9f1f17f0f"
SRC_URI[patch002.md5sum] = "765e14cff12c7284009772e8e24f2fe0"
SRC_URI[patch002.sha256sum] = "7020a0183e17a7233e665b979c78c184ea369cfaf3e8b4b11f5547ecb7c13c53"
SRC_URI[patch003.md5sum] = "49e7da93bf07f510a2eb6bb43ac3e5a2"
SRC_URI[patch003.sha256sum] = "51df5a9192fdefe0ddca4bdf290932f74be03ffd0503a3d112e4199905e718b2"
SRC_URI[patch004.md5sum] = "4557d674ab5831a5fa98052ab19edaf4"
SRC_URI[patch004.sha256sum] = "ad080a30a4ac6c1273373617f29628cc320a35c8cd06913894794293dc52c8b3"
SRC_URI[patch005.md5sum] = "cce96dd77cdd1d293beec10848f6cbb5"
SRC_URI[patch005.sha256sum] = "221e4b725b770ad0bb6924df3f8d04f89eeca4558f6e4c777dfa93e967090529"
SRC_URI[patch006.md5sum] = "d3379f8d8abce5c6ee338f931ad008d5"
SRC_URI[patch006.sha256sum] = "6a8e2e2a6180d0f1ce39dcd651622fb6d2fd05db7c459f64ae42d667f1e344b8"
SRC_URI[patch007.md5sum] = "ec38c76ca439ca7f9c178e9baede84fc"
SRC_URI[patch007.sha256sum] = "de1ccc07b7bfc9e25243ad854f3bbb5d3ebf9155b0477df16aaf00a7b0d5edaf"
SRC_URI[patch008.md5sum] = "e0ba18c1e3b94f905da9b5bf9d38b58b"
SRC_URI[patch008.sha256sum] = "86144700465933636d7b945e89b77df95d3620034725be161ca0ca5a42e239ba"
SRC_URI[patch009.md5sum] = "e952d4f44e612048930c559d90eb99bb"
SRC_URI[patch009.sha256sum] = "0b6bdd1a18a0d20e330cc3bc71e048864e4a13652e29dc0ebf3918bea729343c"
SRC_URI[patch010.md5sum] = "57b5b35955d68f9a09dbef6b86d2c782"
SRC_URI[patch010.sha256sum] = "8465c6f2c56afe559402265b39d9e94368954930f9aa7f3dfa6d36dd66868e06"
SRC_URI[patch011.md5sum] = "cc896e1fa696b93ded568e557e2392d5"
SRC_URI[patch011.sha256sum] = "dd56426ef7d7295e1107c0b3d06c192eb9298f4023c202ca2ba6266c613d170d"
SRC_URI[patch012.md5sum] = "fa47fbfa56fb7e9e5367f19a9df5fc9e"
SRC_URI[patch012.sha256sum] = "fac271d2bf6372c9903e3b353cb9eda044d7fe36b5aab52f21f3f21cd6a2063e"

BBCLASSEXTEND = "nativesdk"
