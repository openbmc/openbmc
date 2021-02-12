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

# When upgrading to 2.37, please make sure there is no trailing .0, so
# that upstream version check can work correctly.
PV = "2.36"
CVE_VERSION = "2.36"
BINUPV = "${@binutils_branch_version(d)}"
#BRANCH = "binutils-${BINUPV}-branch"
BRANCH ?= "binutils-2_36-branch"

UPSTREAM_CHECK_GITTAGREGEX = "binutils-(?P<pver>\d+_(\d_?)*)"

SRCREV ?= "fe0e833171513c1d89668bc5f454192d2db39bce"
BINUTILS_GIT_URI ?= "git://sourceware.org/git/binutils-gdb.git;branch=${BRANCH};protocol=git"
SRC_URI = "\
     ${BINUTILS_GIT_URI} \
     file://0004-configure-widen-the-regexp-for-SH-architectures.patch \
     file://0005-Point-scripts-location-to-libdir.patch \
     file://0006-Only-generate-an-RPATH-entry-if-LD_RUN_PATH-is-not-e.patch \
     file://0007-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
     file://0008-warn-for-uses-of-system-directories-when-cross-linki.patch \
     file://0009-Change-default-emulation-for-mips64-linux.patch \
     file://0010-Add-support-for-Netlogic-XLP.patch \
     file://0011-fix-the-incorrect-assembling-for-ppc-wait-mnemonic.patch \
     file://0012-Detect-64-bit-MIPS-targets.patch \
     file://0013-Use-libtool-2.4.patch \
     file://0014-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
     file://0015-sync-with-OE-libtool-changes.patch \
     file://0016-Check-for-clang-before-checking-gcc-version.patch \
"
S  = "${WORKDIR}/git"
