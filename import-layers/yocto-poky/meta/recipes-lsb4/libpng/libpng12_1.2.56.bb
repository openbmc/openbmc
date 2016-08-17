SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7f289af309d98b46321d0f9892def16 \
                    file://png.h;beginline=19;endline=109;md5=10c940148fe379a535b310c1c54e609c"
DEPENDS = "zlib"

PN = "libpng12"
S = "${WORKDIR}/libpng-${PV}"

SRC_URI = "${GENTOO_MIRROR}/libpng-${PV}.tar.xz"

SRC_URI[md5sum] = "868562bd1c58b76ed8703f135a2e439a"
SRC_URI[sha256sum] = "24ce54581468b937734a6ecc86f7e121bc46a90d76a0d948dca08f32ee000dbe"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libpng/files/libpng12/"
UPSTREAM_CHECK_REGEX = "/libpng12/(?P<pver>(\d+[\.\-_]*)+)/"

BINCONFIG_GLOB = "${PN}-config"

inherit autotools binconfig pkgconfig

do_install_append() {
	# The follow link files link to corresponding png12*.h and libpng12* files
	# They conflict with higher verison, so drop them
	unlink ${D}/${includedir}/png.h
	unlink ${D}/${includedir}/pngconf.h

	unlink ${D}/${libdir}/libpng.la
	unlink ${D}/${libdir}/libpng.so
	unlink ${D}/${libdir}/libpng.a || true
	unlink ${D}/${libdir}/pkgconfig/libpng.pc

	unlink ${D}/${bindir}/libpng-config
}
