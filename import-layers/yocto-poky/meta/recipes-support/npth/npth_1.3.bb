SUMMARY = "New GNU Portable Threads library"
HOMEPAGE = "http://www.gnupg.org/software/pth/"
SECTION = "libs"
LICENSE = "LGPLv3+ & GPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe\
    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6\
    "
UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/npth/npth-${PV}.tar.bz2 \
           file://pkgconfig.patch \
          "

SRC_URI[md5sum] = "efe1524c53670b5755dc27893d2d68a0"
SRC_URI[sha256sum] = "bca81940436aed0734eb8d0ff8b179e04cc8c087f5625204419f5f45d736a82a"

BINCONFIG = "${bindir}/npth-config"

inherit autotools binconfig-disabled

FILES_${PN} = "${libdir}/libnpth.so.*"
FILES_${PN}-dev += "${bindir}/npth-config"
