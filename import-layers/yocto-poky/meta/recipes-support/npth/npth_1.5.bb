SUMMARY = "New GNU Portable Threads library"
HOMEPAGE = "http://www.gnupg.org/software/pth/"
SECTION = "libs"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING.LIB;md5=2caced0b25dfefd4c601d92bd15116de\
    "
UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/npth/npth-${PV}.tar.bz2 \
           file://pkgconfig.patch \
          "

SRC_URI[md5sum] = "9ba2dc4302d2f32c66737c43ed191b1b"
SRC_URI[sha256sum] = "294a690c1f537b92ed829d867bee537e46be93fbd60b16c04630fbbfcd9db3c2"

BINCONFIG = "${bindir}/npth-config"

inherit autotools binconfig-disabled

FILES_${PN} = "${libdir}/libnpth.so.*"
FILES_${PN}-dev += "${bindir}/npth-config"
