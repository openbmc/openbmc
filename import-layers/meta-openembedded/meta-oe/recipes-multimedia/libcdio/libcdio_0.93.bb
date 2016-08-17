ESCRIPTION = "The GNU Compact Disc Input and Control library (libcdio) contains a library for CD-ROM and CD image access."
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
SECTION = "libs"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "ncurses"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "d154476feaac5a7b5f180e83eaf3d689"
SRC_URI[sha256sum] = "4972cd22fd8d0e8bff922d35c7a645be0db0ab0e7b3dfaecc9cd8272429d6975"

inherit autotools pkgconfig

PACKAGES += "${PN}-utils"

FILES_${PN} = "${libdir}/${PN}${SOLIB}"
FILES_${PN}-utils = "${bindir}/*"

python populate_packages_prepend () {
    glibdir = d.expand('${libdir}')
    do_split_packages(d, glibdir, '^lib(.*)\.so\..*', 'lib%s', 'gstreamer %s library', extra_depends='', allow_links=True)
}

