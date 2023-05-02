require bash.inc

# GPL-2.0-or-later (< 4.0), GPL-3.0-or-later (>= 4.0)
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           file://execute_cmd.patch \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://run-bash-ptests \
           file://fix-run-builtins.patch \
           file://use_aclocal.patch \
           file://makerace.patch \
           file://makerace2.patch \
           file://CVE-2022-3715.patch \
           "

SRC_URI[tarball.sha256sum] = "5bac17218d3911834520dad13cd1f85ab944e1c09ae1aba55906be1f8192f558"

DEBUG_OPTIMIZATION:append:armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION:append:armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

BBCLASSEXTEND = "nativesdk"
