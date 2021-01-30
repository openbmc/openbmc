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
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-008;apply=yes;striplevel=0;name=patch008 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-009;apply=yes;striplevel=0;name=patch009 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-010;apply=yes;striplevel=0;name=patch010 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-011;apply=yes;striplevel=0;name=patch011 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-012;apply=yes;striplevel=0;name=patch012 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-013;apply=yes;striplevel=0;name=patch013 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-014;apply=yes;striplevel=0;name=patch014 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-015;apply=yes;striplevel=0;name=patch015 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-016;apply=yes;striplevel=0;name=patch016 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-017;apply=yes;striplevel=0;name=patch017 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash50-018;apply=yes;striplevel=0;name=patch018 \
           file://execute_cmd.patch \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://run-bash-ptests \
           file://fix-run-builtins.patch \
           file://CVE-2019-18276.patch \
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
SRC_URI[patch008.md5sum] = "dd7cf7a784d1838822cad8d419315991"
SRC_URI[patch008.sha256sum] = "eec64588622a82a5029b2776e218a75a3640bef4953f09d6ee1f4199670ad7e3"
SRC_URI[patch009.md5sum] = "c1b3e937cd6dccbb7fd772f32812a0da"
SRC_URI[patch009.sha256sum] = "ed3ca21767303fc3de93934aa524c2e920787c506b601cc40a4897d4b094d903"
SRC_URI[patch010.md5sum] = "19b41e73b03602d0e261c471b53e670c"
SRC_URI[patch010.sha256sum] = "d6fbc325f0b5dc54ddbe8ee43020bced8bd589ddffea59d128db14b2e52a8a11"
SRC_URI[patch011.md5sum] = "414339330a3634137081a97f2c8615a8"
SRC_URI[patch011.sha256sum] = "2c4de332b91eaf797abbbd6c79709690b5cbd48b12e8dfe748096dbd7bf474ea"
SRC_URI[patch012.md5sum] = "1870268f62b907221b078ad109e1fa94"
SRC_URI[patch012.sha256sum] = "2943ee19688018296f2a04dbfe30b7138b889700efa8ff1c0524af271e0ee233"
SRC_URI[patch013.md5sum] = "40d923af4b952b01983ed4c889ae2653"
SRC_URI[patch013.sha256sum] = "f5d7178d8da30799e01b83a0802018d913d6aa972dd2ddad3b927f3f3eb7099a"
SRC_URI[patch014.md5sum] = "57857b22053c8167677e5e5ac5c6669b"
SRC_URI[patch014.sha256sum] = "5d6eee6514ee6e22a87bba8d22be0a8621a0ae119246f1c5a9a35db1f72af589"
SRC_URI[patch015.md5sum] = "c4c6ea23d09a74eaa9385438e48fdf02"
SRC_URI[patch015.sha256sum] = "a517df2dda93b26d5cbf00effefea93e3a4ccd6652f152f4109170544ebfa05e"
SRC_URI[patch016.md5sum] = "a682ed6fa2c2e7a7c3ba6bdeada07fb5"
SRC_URI[patch016.sha256sum] = "ffd1d7a54a99fa7f5b1825e4f7e95d8c8876bc2ca151f150e751d429c650b06d"
SRC_URI[patch017.md5sum] = "d9dcaa1d8e7a24850449a1aac43a12a9"
SRC_URI[patch017.sha256sum] = "4cf3b9fafb8a66d411dd5fc9120032533a4012df1dc6ee024c7833373e2ddc31"
SRC_URI[patch018.md5sum] = "a64d950d5de72ae590455b13e6afefcb"
SRC_URI[patch018.sha256sum] = "7c314e375a105a6642e8ed44f3808b9def89d15f7492fe2029a21ba9c0de81d3"


DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

BBCLASSEXTEND = "nativesdk"
