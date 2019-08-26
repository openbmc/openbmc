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

# Actual upstream version is 2.32 (without the .0), so we have to set
# UPSTREAM_VERSION_UNKNOWN to avoid the version check failure. The line can
# be removed when a new version of binutils is released (if the PV is then
# correctly set to match the upstream version tag).
UPSTREAM_VERSION_UNKNOWN = "1"
PV = "2.32.0"
CVE_VERSION = "2.32"
BINUPV = "${@binutils_branch_version(d)}"
#BRANCH = "binutils-${BINUPV}-branch"
BRANCH ?= "binutils-2_32-branch"

UPSTREAM_CHECK_GITTAGREGEX = "binutils-(?P<pver>\d+_(\d_?)*)"

SRCREV ?= "b8c1f608db9ef3edd483d21a921d1fbedc71df6f"
BINUTILS_GIT_URI ?= "git://sourceware.org/git/binutils-gdb.git;branch=${BRANCH};protocol=git"
SRC_URI = "\
     ${BINUTILS_GIT_URI} \
     file://0003-configure-widen-the-regexp-for-SH-architectures.patch \
     file://0004-Point-scripts-location-to-libdir.patch \
     file://0005-Only-generate-an-RPATH-entry-if-LD_RUN_PATH-is-not-e.patch \
     file://0006-Use-libtool-2.4.patch \
     file://0007-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
     file://0008-warn-for-uses-of-system-directories-when-cross-linki.patch \
     file://0009-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
     file://0010-Change-default-emulation-for-mips64-linux.patch \
     file://0011-Add-support-for-Netlogic-XLP.patch \
     file://0012-fix-the-incorrect-assembling-for-ppc-wait-mnemonic.patch \
     file://0013-Detect-64-bit-MIPS-targets.patch \
     file://0014-sync-with-OE-libtool-changes.patch \
     file://0015-binutils-enable-x86_64-pep-for-producing-EFI-binarie.patch \
     file://CVE-2019-9074.patch \
     file://CVE-2019-9075.patch \
     file://CVE-2019-9076.patch \
     file://CVE-2019-9077.patch \
     file://CVE-2019-9071.patch \
     file://CVE-2019-12972.patch \
     file://CVE-2019-14250.patch \
     file://CVE-2019-14444.patch \
"
S  = "${WORKDIR}/git"

do_configure_prepend () {
        rm -rf ${S}/gdb ${S}/libdecnumber ${S}/readline ${S}/sim
}
