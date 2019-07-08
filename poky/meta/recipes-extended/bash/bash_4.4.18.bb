require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-019;apply=yes;striplevel=0;name=patch019 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-020;apply=yes;striplevel=0;name=patch020 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-021;apply=yes;striplevel=0;name=patch021 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-022;apply=yes;striplevel=0;name=patch022 \
           ${GNU_MIRROR}/bash/bash-4.4-patches/bash44-023;apply=yes;striplevel=0;name=patch023 \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           file://fix-run-builtins.patch \
           file://0001-help-fix-printf-format-security-warning.patch \
           file://pathexp-dep.patch \
           "

SRC_URI[tarball.md5sum] = "518e2c187cc11a17040f0915dddce54e"
SRC_URI[tarball.sha256sum] = "604d9eec5e4ed5fd2180ee44dd756ddca92e0b6aa4217bbab2b6227380317f23"

SRC_URI[patch019.md5sum] = "8f43e1d277b02f3319a34c1cd4a4ff3e"
SRC_URI[patch019.sha256sum] = "27170d6edfe8819835407fdc08b401d2e161b1400fe9d0c5317a51104c89c11e"
SRC_URI[patch020.md5sum] = "5217ff08c444446ec306dce60437c288"
SRC_URI[patch020.sha256sum] = "1840e2cbf26ba822913662f74037594ed562361485390c52813b38156c99522c"
SRC_URI[patch021.md5sum] = "282c7d9b38da8005d25b4f816328a2f4"
SRC_URI[patch021.sha256sum] = "bd8f59054a763ec1c64179ad5cb607f558708a317c2bdb22b814e3da456374c1"
SRC_URI[patch022.md5sum] = "0b709c9d7f8e6cf267a8b863efb899f7"
SRC_URI[patch022.sha256sum] = "45331f0936e36ab91bfe44b936e33ed8a1b1848fa896e8a1d0f2ef74f297cb79"
SRC_URI[patch023.md5sum] = "fe2e0ca4cf9409ff0e9428e1236f983e"
SRC_URI[patch023.sha256sum] = "4fec236f3fbd3d0c47b893fdfa9122142a474f6ef66c20ffb6c0f4864dd591b6"

DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

BBCLASSEXTEND = "nativesdk"
