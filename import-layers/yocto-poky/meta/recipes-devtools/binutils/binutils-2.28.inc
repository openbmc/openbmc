LIC_FILES_CHKSUM="\
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\
    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674\
    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504\
    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6\
    file://gas/COPYING;md5=d32239bcb673463ab874e80d47fae504\
    file://include/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\
    file://include/COPYING3;md5=d32239bcb673463ab874e80d47fae504\
    file://libiberty/COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7\
    file://bfd/COPYING;md5=d32239bcb673463ab874e80d47fae504\
    "

def binutils_branch_version(d):
    pvsplit = d.getVar('PV').split('.')
    return pvsplit[0] + "_" + pvsplit[1]

BINUPV = "${@binutils_branch_version(d)}"

UPSTREAM_CHECK_GITTAGREGEX = "binutils-(?P<pver>\d+_(\d_?)*)"

SRCREV = "354199c7692c1bed53a2a15f0e4d531457e95f17"
SRC_URI = "\
     git://sourceware.org/git/binutils-gdb.git;branch=binutils-${BINUPV}-branch;protocol=git \
     file://0003-gprof-add-uclibc-support-to-configure.patch \
     file://0004-Point-scripts-location-to-libdir.patch \
     file://0005-Only-generate-an-RPATH-entry-if-LD_RUN_PATH-is-not-e.patch \
     file://0006-Explicitly-link-with-libm-on-uclibc.patch \
     file://0007-Use-libtool-2.4.patch \
     file://0008-Add-the-armv5e-architecture-to-binutils.patch \
     file://0009-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
     file://0010-warn-for-uses-of-system-directories-when-cross-linki.patch \
     file://0011-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
     file://0012-Change-default-emulation-for-mips64-linux.patch \
     file://0013-Add-support-for-Netlogic-XLP.patch \
     file://0014-fix-the-incorrect-assembling-for-ppc-wait-mnemonic.patch \
     file://0015-sync-with-OE-libtool-changes.patch \
     file://0016-Detect-64-bit-MIPS-targets.patch \
     file://CVE-2017-6965.patch \
     file://CVE-2017-6966.patch \
     file://0017-bfd-Improve-lookup-of-file-line-information-for-erro.patch \
     file://0018-PR-21409-segfault-in-_bfd_dwarf2_find_nearest_line.patch \
     file://CVE-2017-6969.patch \
     file://CVE-2017-6969_2.patch \
     file://CVE-2017-7209.patch \
     file://CVE-2017-7210.patch \
     file://CVE-2017-7223.patch \
     file://CVE-2017-7614.patch \
     file://CVE-2017-8393.patch \
     file://CVE-2017-8394.patch \
     file://CVE-2017-8395.patch \
     file://CVE-2017-8396_8397.patch \
     file://CVE-2017-8398.patch \
     file://CVE-2017-8421.patch \
     file://CVE-2017-9038_9044.patch \
     file://CVE-2017-9039.patch \
     file://CVE-2017-9040_9042.patch \
     file://CVE-2017-9742.patch \
     file://CVE-2017-9744.patch \
     file://CVE-2017-9745.patch \
     file://CVE-2017-9746.patch \
     file://CVE-2017-9747.patch \
     file://CVE-2017-9748.patch \
     file://CVE-2017-9749.patch \
     file://CVE-2017-9750.patch \
     file://CVE-2017-9751.patch \
     file://CVE-2017-9752.patch \
     file://CVE-2017-9753.patch \
     file://CVE-2017-9755.patch \
     file://CVE-2017-9756.patch \
     file://CVE-2017-9954.patch \
     file://CVE-2017-9955_1.patch \
     file://CVE-2017-9955_2.patch \
     file://CVE-2017-9955_3.patch \
     file://CVE-2017-9955_4.patch \
     file://CVE-2017-9955_5.patch \
     file://CVE-2017-9955_6.patch \
     file://CVE-2017-9955_7.patch \
     file://CVE-2017-9955_8.patch \
     file://CVE-2017-9955_9.patch \
"
S  = "${WORKDIR}/git"

do_configure_prepend () {
        rm -rf ${S}/gdb ${S}/libdecnumber ${S}/readline ${S}/sim
}
