DESCRIPTION = "libTorrent is a BitTorrent library written in C++ for *nix, \
with a focus on high performance and good code."
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "zlib libsigc++-2.0 openssl cppunit"

SRC_URI = "git://github.com/rakshasa/libtorrent \
           file://don-t-run-code-while-configuring-package.patch \
           file://0001-implement-64bit-atomic-for-mips.patch \
           file://0001-Define-64bit-atomic-helpers-for-ppc-32-bit.patch \
           "
SRCREV = "074f6ec51a2e4bc5c6140b4dad13c9bb56f3f0dc"

PV = "0.13.7+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-zlib=${STAGING_EXECPREFIXDIR}"

do_configure_prepend() {
    (cd ${S}; ./autogen.sh; cd -)
}
