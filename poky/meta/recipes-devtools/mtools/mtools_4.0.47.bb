SUMMARY = "Utilities to access MS-DOS disks without mounting them"
DESCRIPTION = "Mtools is a collection of utilities to access MS-DOS disks from GNU and Unix without mounting them."
HOMEPAGE = "http://www.gnu.org/software/mtools/"
SECTION = "optional"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "virtual/libiconv"

RDEPENDS:${PN}:libc-glibc = "glibc-gconv-ibm850"
RRECOMMENDS:${PN}:libc-glibc = "\
	glibc-gconv-ibm437 \
	glibc-gconv-ibm737 \
	glibc-gconv-ibm775 \
	glibc-gconv-ibm851 \
	glibc-gconv-ibm852 \
	glibc-gconv-ibm855 \
	glibc-gconv-ibm857 \
	glibc-gconv-ibm860 \
	glibc-gconv-ibm861 \
	glibc-gconv-ibm862 \
	glibc-gconv-ibm863 \
	glibc-gconv-ibm865 \
	glibc-gconv-ibm866 \
	glibc-gconv-ibm869 \
	"
SRC_URI[sha256sum] = "31aa06078cc3f50591b95e71a909c56dd179d87e9cbdc07bf435e595bd7cc7ff"

SRC_URI = "${GNU_MIRROR}/mtools/mtools-${PV}.tar.bz2 \
           file://mtools-makeinfo.patch \
           file://clang_UNUSED.patch \
           "

SRC_URI:append:class-native = " file://disable-hardcoded-configs.patch"

inherit autotools texinfo

EXTRA_OECONF = "--without-x"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libbsd] = "ac_cv_lib_bsd_main=yes,ac_cv_lib_bsd_main=no,libbsd"

do_install:prepend () {
    # Create bindir to fix parallel installation issues
    mkdir -p ${D}/${bindir}
    mkdir -p ${D}/${datadir}
}
