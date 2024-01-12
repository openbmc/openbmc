DESCRIPTION = "PDF transformation/inspection software"
HOMEPAGE = "http://qpdf.sourceforge.net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "zlib jpeg ${@bb.utils.contains('PACKAGECONFIG', 'gnutls', 'gnutls', 'openssl', d)}"

SRC_URI = "git://github.com/qpdf/qpdf.git;protocol=https;branch=main"
SRCREV = "986d2485784d57d7a84cc5af50e67bde827b0dc9"

inherit cmake pkgconfig gettext

EXTRA_OECMAKE = ' \
	-DRANDOM_DEVICE="/dev/random" \
	-DBUILD_STATIC_LIBS=OFF \
	-DALLOW_CRYPTO_NATIVE=OFF \
	-DUSE_IMPLICIT_CRYPTO=OFF \
'

LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

S="${WORKDIR}/git"

PACKAGECONFIG ?= "gnutls"
PACKAGECONFIG[gnutls] = "-DREQUIRE_CRYPTO_GNUTLS=ON,-DREQUIRE_CRYPTO_OPENSSL=ON"

do_install:append() {
    # Change the fully defined path on the target
    sed -i -e 's|${STAGING_LIBDIR}|${libdir}|g' ${D}${libdir}/cmake/${BPN}/libqpdfTargets.cmake
}
