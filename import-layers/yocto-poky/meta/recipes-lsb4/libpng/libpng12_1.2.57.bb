SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=597b8a91994a3e27ae6aa79bf02677d9 \
                    file://png.h;beginline=19;endline=109;md5=166406397718925b660f0033f7558ef7"
DEPENDS = "zlib"

PN = "libpng12"
S = "${WORKDIR}/libpng-${PV}"

SRC_URI = "${GENTOO_MIRROR}/libpng-${PV}.tar.xz"

SRC_URI[md5sum] = "307052e5e8af97b82b17b64fb1b3677a"
SRC_URI[sha256sum] = "0f4620e11fa283fedafb474427c8e96bf149511a1804bdc47350963ae5cf54d8"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libpng/files/libpng12/"
UPSTREAM_CHECK_REGEX = "/libpng12/(?P<pver>(\d+[\.\-_]*)+)/"

BINCONFIG_GLOB = "${PN}-config"

inherit autotools binconfig pkgconfig

do_install_append() {
	# The follow link files link to corresponding png12*.h and libpng12* files
	# They conflict with higher verison, so drop them
	rm ${D}/${includedir}/png.h
	rm ${D}/${includedir}/pngconf.h

	rm ${D}/${libdir}/libpng.la
	rm ${D}/${libdir}/libpng.so
	rm ${D}/${libdir}/libpng.a || true
	rm ${D}/${libdir}/pkgconfig/libpng.pc

	rm ${D}/${bindir}/libpng-config
}
