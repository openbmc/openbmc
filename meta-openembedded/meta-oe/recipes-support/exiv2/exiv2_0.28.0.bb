SUMMARY = "Exif, Iptc and XMP metadata manipulation library and tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "zlib expat brotli libinih"

SRC_URI = "https://github.com/Exiv2/${BPN}/releases/download/v${PV}/${BP}-Source.tar.gz"
SRC_URI[sha256sum] = "89af3b5ef7277753ef7a7b5374ae017c6b9e304db3b688f1948e73e103491f3d"
# Once patch is obsolete (project should be aware due to PRs), dos2unix can be removed either
# inherit dos2unix
S = "${WORKDIR}/${BP}-Source"

inherit cmake gettext

do_install:append:class-target() {
    # reproducibility: remove build host path
    sed -i ${D}${libdir}/cmake/exiv2/exiv2Config.cmake \
        -e 's:${STAGING_DIR_HOST}::g'
}
