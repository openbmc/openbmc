DESCRIPTION = "libTorrent is a BitTorrent library written in C++ for *nix, \
with a focus on high performance and good code."
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "zlib libsigc++-2.0 openssl cppunit"

SRC_URI = "git://github.com/rakshasa/libtorrent;branch=master;protocol=https \
           file://don-t-run-code-while-configuring-package.patch \
           "
SRCREV = "756f70010779927dc0691e1e722ed433d5d295e1"

PV = "0.13.8"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "instrumentation"

PACKAGECONFIG_remove_mipsarch = "instrumentation"
PACKAGECONFIG_remove_powerpc = "instrumentation"

PACKAGECONFIG[instrumentation] = "--enable-instrumentation,--disable-instrumentation,"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-zlib=${STAGING_EXECPREFIXDIR}"

do_configure_prepend() {
    (cd ${S}; ./autogen.sh; cd -)
}
