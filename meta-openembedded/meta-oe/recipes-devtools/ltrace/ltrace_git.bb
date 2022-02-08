SUMMARY = "ltrace intercepts and records dynamic library calls"

DESCRIPTION = "ltrace intercepts and records dynamic library calls \
which are called by an executed process and the signals received by that process. \
It can also intercept and print the system calls executed by the program.\
"
HOMEPAGE = "http://ltrace.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

PE = "1"
PV = "7.91+git${SRCPV}"
SRCREV = "c22d359433b333937ee3d803450dc41998115685"

DEPENDS = "elfutils"
SRC_URI = "git://github.com/sparkleholic/ltrace.git;branch=master;protocol=http;protocol=https \
           file://configure-allow-to-disable-selinux-support.patch \
           file://0001-replace-readdir_r-with-readdir.patch \
           file://0001-Use-correct-enum-type.patch \
           file://0002-Fix-const-qualifier-error.patch \
           file://0001-ARM-code-has-unreachable-code-after-switch-statement.patch \
           file://0001-Fix-tautological-compare-warning.patch \
           file://0001-Add-support-for-mips64-n32-n64.patch \
           file://0001-configure-Recognise-linux-musl-as-a-host-OS.patch \
           file://0001-mips-plt.c-Delete-include-error.h.patch \
           file://0001-move-fprintf-into-same-block-where-modname-and-symna.patch \
           file://0001-hook-Do-not-append-int-to-std-string.patch \
           file://include_unistd_nr.patch \
           file://0001-Bug-fix-for-data-type-length-judgment.patch \
           file://0001-ensure-the-struct-pointers-are-null-initilized.patch \
           "
S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[unwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,libselinux"

COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"

do_configure_prepend () {
    ( cd ${S}; ./autogen.sh )
}
