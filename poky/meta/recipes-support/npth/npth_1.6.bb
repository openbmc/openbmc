SUMMARY = "New GNU Portable Threads library"
DESCRIPTION = "nPth is a library to provide the GNU Pth API and thus a non-preemptive threads implementation. "
HOMEPAGE = "https://www.gnu.org/software/pth/"
SECTION = "libs"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING.LIB;md5=2caced0b25dfefd4c601d92bd15116de\
    "
UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/npth/npth-${PV}.tar.bz2 \
           file://pkgconfig.patch \
          "

SRC_URI[md5sum] = "375d1a15ad969f32d25f1a7630929854"
SRC_URI[sha256sum] = "1393abd9adcf0762d34798dc34fdcf4d0d22a8410721e76f1e3afcd1daa4e2d1"

BINCONFIG = "${bindir}/npth-config"

inherit autotools binconfig-disabled multilib_header

FILES_${PN} = "${libdir}/libnpth.so.*"
FILES_${PN}-dev += "${bindir}/npth-config"

do_install_append() {
    oe_multilib_header npth.h
}

BBCLASSEXTEND = "native nativesdk"
