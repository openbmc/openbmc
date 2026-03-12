SUMMARY = "library to read digital audio CDs with error correction"
HOMEPAGE = "http://www.gnu.org/software/libcdio/"
BUGTRUCKER = "https://github.com/libcdio/libcdio-paranoia/issues"
SECTION = "libs"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "libcdio"

SRC_URI = "${GITHUB_BASE_URI}/download/release-${PV}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "186892539dedd661276014d71318c8c8f97ecb1250a86625256abd4defbf0d0c"

GITHUB_BASE_URI = "https://github.com/libcdio/${BPN}/releases/"

UPSTREAM_CHECK_REGEX = "release-(?P<pver>\d+(\.\d+)+\+\d+(\.\d+)+)"

inherit autotools pkgconfig github-releases

PACKAGES += "${PN}-utils"

FILES:${PN} = "${libdir}/${BPN}${SOLIB}"
FILES:${PN}-utils = "${bindir}/*"

python libcdio_split_packages() {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, r'^lib(.*)\.so\..*', 'lib%s', 'libcdio %s library', extra_depends='', allow_links=True)
}

PACKAGESPLITFUNCS =+ "libcdio_split_packages"
