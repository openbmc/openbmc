require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-001;apply=yes;striplevel=0;name=patch001 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-002;apply=yes;striplevel=0;name=patch002 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-003;apply=yes;striplevel=0;name=patch003 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-004;apply=yes;striplevel=0;name=patch004 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-005;apply=yes;striplevel=0;name=patch005 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-006;apply=yes;striplevel=0;name=patch006 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-007;apply=yes;striplevel=0;name=patch007 \
           file://execute_cmd.patch \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://run-bash-ptests \
           file://fix-run-builtins.patch \
           "

SRC_URI[tarball.md5sum] = "2b44b47b905be16f45709648f671820b"
SRC_URI[tarball.sha256sum] = "b4a80f2ac66170b2913efbfb9f2594f1f76c7b1afd11f799e22035d63077fb4d"

SRC_URI[patch001.md5sum] = "b026862ab596a5883bb4f0d1077a3819"
SRC_URI[patch001.sha256sum] = "f2fe9e1f0faddf14ab9bfa88d450a75e5d028fedafad23b88716bd657c737289"
SRC_URI[patch002.md5sum] = "2f4a7787365790ae57f36b311701ea7e"
SRC_URI[patch002.sha256sum] = "87e87d3542e598799adb3e7e01c8165bc743e136a400ed0de015845f7ff68707"
SRC_URI[patch003.md5sum] = "af7f2dd93fd5429fb5e9a642ff74f87d"
SRC_URI[patch003.sha256sum] = "4eebcdc37b13793a232c5f2f498a5fcbf7da0ecb3da2059391c096db620ec85b"
SRC_URI[patch004.md5sum] = "b60545b273bfa4e00a760f2c648bed9c"
SRC_URI[patch004.sha256sum] = "14447ad832add8ecfafdce5384badd933697b559c4688d6b9e3d36ff36c62f08"
SRC_URI[patch005.md5sum] = "875a0bedf48b74e453e3997c84b5d8a4"
SRC_URI[patch005.sha256sum] = "5bf54dd9bd2c211d2bfb34a49e2c741f2ed5e338767e9ce9f4d41254bf9f8276"
SRC_URI[patch006.md5sum] = "4a8ee95adb72c3aba03d9e8c9f96ece6"
SRC_URI[patch006.sha256sum] = "d68529a6ff201b6ff5915318ab12fc16b8a0ebb77fda3308303fcc1e13398420"
SRC_URI[patch007.md5sum] = "411560d81fde2dc5b17b83c3f3b58c6f"
SRC_URI[patch007.sha256sum] = "17b41e7ee3673d8887dd25992417a398677533ab8827938aa41fad70df19af9b"

DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

BBCLASSEXTEND = "nativesdk"
