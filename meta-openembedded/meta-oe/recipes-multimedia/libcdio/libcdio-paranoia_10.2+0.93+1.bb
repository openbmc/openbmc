SUMMARY = "library to read digital audio CDs with error correction"
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
SECTION = "libs"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "libcdio"

SRC_URI = "${GNU_MIRROR}/libcdio/${BP}.tar.bz2"
SRC_URI[md5sum] = "0255aa50e660db7f2c39658b9c565814"
SRC_URI[sha256sum] = "ec1d9b1d5a28cc042f2cb33a7cc0a2b5ce5525f102bc4c15db1fac322559a493"

inherit autotools pkgconfig

PACKAGES += "${PN}-utils"

FILES_${PN} = "${libdir}/${BPN}${SOLIB}"
FILES_${PN}-utils = "${bindir}/*"

python libcdio_split_packages() {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, '^lib(.*)\.so\..*', 'lib%s', 'libcdio %s library', extra_depends='', allow_links=True)
}

PACKAGESPLITFUNCS =+ "libcdio_split_packages"
