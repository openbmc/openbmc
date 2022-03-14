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

# When upgrading to 2.37, please make sure there is no trailing .0, so
# that upstream version check can work correctly.
PV = "2.37"
CVE_VERSION = "2.37"
SRCBRANCH ?= "binutils-2_37-branch"

UPSTREAM_CHECK_GITTAGREGEX = "binutils-(?P<pver>\d+_(\d_?)*)"

SRCREV ?= "87d4632d36323091e731eb07b8aa65f90293da66"
BINUTILS_GIT_URI ?= "git://sourceware.org/git/binutils-gdb.git;branch=${SRCBRANCH};protocol=git"
SRC_URI = "\
     ${BINUTILS_GIT_URI} \
     file://0005-Point-scripts-location-to-libdir.patch \
     file://0006-Only-generate-an-RPATH-entry-if-LD_RUN_PATH-is-not-e.patch \
     file://0007-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
     file://0008-warn-for-uses-of-system-directories-when-cross-linki.patch \
     file://0011-fix-the-incorrect-assembling-for-ppc-wait-mnemonic.patch \
     file://0013-Use-libtool-2.4.patch \
     file://0014-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
     file://0015-sync-with-OE-libtool-changes.patch \
     file://0016-Check-for-clang-before-checking-gcc-version.patch \
     file://0017-bfd-Close-the-file-descriptor-if-there-is-no-archive.patch \
     file://0001-elf-Discard-input-.note.gnu.build-id-sections.patch \
     file://0001-CVE-2021-42574.patch \
"
S  = "${WORKDIR}/git"
