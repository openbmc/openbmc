SUMMARY = "The GNU Compact Disc Input and Control library (libcdio) contains a library for CD-ROM and CD image access."
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
SECTION = "libs"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "0cb25905113b930e4539d2f4eb6574b0"
SRC_URI[sha256sum] = "1b481b5da009bea31db875805665974e2fc568e2b2afa516f4036733657cf958"

inherit autotools pkgconfig

PACKAGECONFIG ??= "cdda-player"
PACKAGECONFIG[cdda-player] = "--with-cdda-player,--without-cdda-player,ncurses"
PACKAGECONFIG[cddb] = "--enable-cddb,--disable-cddb,libcddb"
PACKAGECONFIG[vcd-info] = "--enable-vcd-info,--disable-vcd-info,vcdimager"

PACKAGES += "${PN}-utils"

FILES_${PN} = "${libdir}/${BPN}${SOLIB}"
FILES_${PN}-utils = "${bindir}/*"

python libcdio_split_packages() {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, '^lib(.*)\.so\..*', 'lib%s', 'libcdio %s library', extra_depends='', allow_links=True)
}

PACKAGESPLITFUNCS =+ "libcdio_split_packages"
