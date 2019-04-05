SUMMARY = "Utilities to access MS-DOS disks without mounting them"
DESCRIPTION = "Mtools is a collection of utilities to access MS-DOS disks from GNU and Unix without mounting them."
HOMEPAGE = "http://www.gnu.org/software/mtools/"
SECTION = "optional"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "virtual/libiconv"

RDEPENDS_${PN}_libc-glibc = "glibc-gconv-ibm850"
RRECOMMENDS_${PN}_libc-glibc = "\
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
SRC_URI[md5sum] = "f750246a65ee6f9d2e82d10b03a0aaaf"
SRC_URI[sha256sum] = "1bc197199ea1c2c317f597879486eeb817f3199b526d94cc6841624768d910df"

SRC_URI = "${GNU_MIRROR}/mtools/mtools-${PV}.tar.bz2 \
           file://mtools-makeinfo.patch \
           file://no-x11.gplv3.patch \
           file://0001-Continue-even-if-fs-size-is-not-divisible-by-sectors.patch \
           file://0001-remove-LOCK_NB-to-use-blocking-request.patch \
           file://clang_UNUSED.patch \
           "

SRC_URI_append_class-native = " file://disable-hardcoded-configs.patch"

inherit autotools texinfo distro_features_check

EXTRA_OECONF = "--without-x"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libbsd] = "ac_cv_lib_bsd_main=yes,ac_cv_lib_bsd_main=no,libbsd"

do_install_prepend () {
    # Create bindir to fix parallel installation issues
    mkdir -p ${D}/${bindir}
    mkdir -p ${D}/${datadir}
}
