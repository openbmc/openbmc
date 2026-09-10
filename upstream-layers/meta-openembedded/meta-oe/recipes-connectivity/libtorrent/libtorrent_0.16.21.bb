DESCRIPTION = "libTorrent is a BitTorrent library written in C++ for *nix, \
with a focus on high performance and good code."
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "zlib curl libsigc++-2.0 openssl cppunit"

SRC_URI = "git://github.com/rakshasa/libtorrent;branch=master;protocol=https;tag=v${PV}"
SRCREV = "306fbbf008183dcfd55fae0a83fff95302c64987"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

CVE_PRODUCT = ""

PACKAGECONFIG ??= "instrumentation"

PACKAGECONFIG:remove:mipsarch = "instrumentation"
PACKAGECONFIG:remove:powerpc = "instrumentation"
PACKAGECONFIG:remove:riscv32 = "instrumentation"

PACKAGECONFIG[instrumentation] = "--enable-instrumentation,--disable-instrumentation,"

inherit autotools pkgconfig
