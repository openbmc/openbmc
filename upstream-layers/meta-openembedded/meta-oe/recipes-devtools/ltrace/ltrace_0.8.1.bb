SUMMARY = "ltrace intercepts and records dynamic library calls"

DESCRIPTION = "ltrace intercepts and records dynamic library calls \
which are called by an executed process and the signals received by that process. \
It can also intercept and print the system calls executed by the program.\
"
HOMEPAGE = "http://ltrace.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

PE = "2"
SRCREV = "7ef6e6097586b751cce298c256a919404dab259d"

DEPENDS = "elfutils"
SRC_URI = "git://gitlab.com/cespedes/ltrace.git;protocol=https;branch=main;tag=${PV} \
           file://configure-allow-to-disable-selinux-support.patch \
           file://0001-Use-correct-enum-type.patch \
           file://0002-Fix-const-qualifier-error.patch \
           file://0001-Add-support-for-mips64-n32-n64.patch \
           file://0001-mips-plt.c-Delete-include-error.h.patch \
           file://0001-move-fprintf-into-same-block-where-modname-and-symna.patch \
           file://0001-hook-Do-not-append-int-to-std-string.patch \
           file://0001-Bug-fix-for-data-type-length-judgment.patch \
           file://0001-ppc-Remove-unused-host_powerpc64-function.patch \
           file://0001-mips-Use-hardcodes-values-for-ABI-syscall-bases.patch \
           file://0001-proc-Make-PROC_PID_FILE-not-use-variable-length-arra.patch \
           file://0001-dwarf_prototypes-return-NULL-from-NEXT_SIBLING-on-er.patch \
           file://0001-trace-fix-1-bit-bitfield-assignments-for-clang-Wsing.patch \
           "
SRC_URI:append:libc-musl = " file://add_ppc64le.patch"


inherit autotools

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[unwind] = "--with-libunwind --without-elfutils,--without-libunwind,libunwind"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,libselinux"

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"

do_configure:prepend () {
    ( cd ${S}; ./autogen.sh )
}
