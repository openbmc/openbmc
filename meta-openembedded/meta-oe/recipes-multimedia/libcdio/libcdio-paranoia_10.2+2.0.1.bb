SUMMARY = "library to read digital audio CDs with error correction"
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
BUGTRUCKER = "https://github.com/rocky/libcdio-paranoia/issues/"
SECTION = "libs"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "libcdio"

SRC_URI = "${GNU_MIRROR}/libcdio/${BP}.tar.bz2"
SRC_URI[sha256sum] = "33b1cf305ccfbfd03b43936975615000ce538b119989c4bec469577570b60e8a"

inherit autotools pkgconfig

PACKAGES += "${PN}-utils"

FILES:${PN} = "${libdir}/${BPN}${SOLIB}"
FILES:${PN}-utils = "${bindir}/*"

python libcdio_split_packages() {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, '^lib(.*)\.so\..*', 'lib%s', 'libcdio %s library', extra_depends='', allow_links=True)
}

PACKAGESPLITFUNCS =+ "libcdio_split_packages"
