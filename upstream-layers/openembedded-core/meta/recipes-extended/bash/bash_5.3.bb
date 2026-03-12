require bash.inc

# GPL-2.0-or-later (< 4.0), GPL-3.0-or-later (>= 4.0)
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://run-bash-ptests \
           file://fix-run-builtins.patch \
           "

SRC_URI[tarball.sha256sum] = "0d5cd86965f869a26cf64f4b71be7b96f90a3ba8b3d74e27e8e9d9d5550f31ba"

DEBUG_OPTIMIZATION:append:armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION:append:armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

CFLAGS += "-std=gnu17"
# mkbuiltins.c is built with native toolchain and needs gnu17 as well:
# http://errors.yoctoproject.org/Errors/Details/853016/
BUILD_CFLAGS += "-std=gnu17"

BBCLASSEXTEND = "nativesdk"
