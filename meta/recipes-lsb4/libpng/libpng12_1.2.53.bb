SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd2a7a13355fcca92ad83bdf7e296011 \
                    file://png.h;beginline=322;endline=436;md5=caf9ee541234c663aeecdcfef2f79ae1"
DEPENDS = "zlib"

PN = "libpng12"
S = "${WORKDIR}/libpng-${PV}"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/libpng/libpng12/older-releases/${PV}/libpng-${PV}.tar.xz"

SRC_URI[md5sum] = "7d18a74e6fd2029aee76ccd00e00a9e6"
SRC_URI[sha256sum] = "b45e49f689e7451bd576569e6a344f7e0d11c02ecbb797f4da0e431526765c0a"

BINCONFIG_GLOB = "${PN}-config"

inherit autotools binconfig pkgconfig

do_install_append() {
	# The follow link files link to corresponding png12*.h and libpng12* files
	# They conflict with higher verison, so drop them
	unlink ${D}/${includedir}/png.h
	unlink ${D}/${includedir}/pngconf.h

	unlink ${D}/${libdir}/libpng.la
	unlink ${D}/${libdir}/libpng.so
	unlink ${D}/${libdir}/libpng.a
	unlink ${D}/${libdir}/pkgconfig/libpng.pc

	unlink ${D}/${bindir}/libpng-config
}
