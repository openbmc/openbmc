SUMMARY = "The GNU Compact Disc Input and Control library (libcdio) contains a library for CD-ROM and CD image access."
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
SECTION = "libs"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BP}.tar.bz2"

SRC_URI[sha256sum] = "53e83d284667535a767fd2d31edad1a6701591960459df373a10f1f21e80a7ed"

inherit autotools pkgconfig github-releases

PACKAGECONFIG ??= "cdda-player"
PACKAGECONFIG[cdda-player] = "--with-cdda-player,--without-cdda-player,ncurses"
PACKAGECONFIG[cddb] = "--enable-cddb,--disable-cddb,libcddb"
PACKAGECONFIG[vcd-info] = "--enable-vcd-info,--disable-vcd-info,vcdimager"

# add -D_LARGEFILE64_SOURCE for 32bit targets
CFLAGS += "${@['-D_LARGEFILE64_SOURCE',''][d.getVar('SITEINFO_BITS') != '32']}"

PACKAGES += "${PN}-utils"

FILES:${PN} = "${libdir}/${BPN}${SOLIB}"
FILES:${PN}-utils = "${bindir}/*"

python libcdio_split_packages() {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, r'^lib(.*)\.so\..*', 'lib%s', 'libcdio %s library', extra_depends='', allow_links=True)
}

PACKAGESPLITFUNCS =+ "libcdio_split_packages"

CVE_STATUS[CVE-2024-36600] = "fixed-version: fixed in v2.3.0"
