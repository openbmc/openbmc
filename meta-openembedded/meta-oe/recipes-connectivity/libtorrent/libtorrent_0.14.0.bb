DESCRIPTION = "libTorrent is a BitTorrent library written in C++ for *nix, \
with a focus on high performance and good code."
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "zlib libsigc++-2.0 openssl cppunit"

SRC_URI = "git://github.com/rakshasa/libtorrent;branch=master;protocol=https"
SRCREV = "08fb6635ba249665cb08a4de1b955288d25c21e1"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

CVE_STATUS[CVE-2009-1760] = "backported-patch: patched in our product"

S = "${WORKDIR}/git"

CVE_PRODUCT = ""

PACKAGECONFIG ??= "instrumentation aligned"

PACKAGECONFIG:remove:mipsarch = "instrumentation"
PACKAGECONFIG:remove:powerpc = "instrumentation"
PACKAGECONFIG:remove:riscv32 = "instrumentation"

PACKAGECONFIG[instrumentation] = "--enable-instrumentation,--disable-instrumentation,"
PACKAGECONFIG[aligned] = "--enable-aligned,--disable-aligned,"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-zlib=${STAGING_EXECPREFIXDIR}"
