SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=890b830b22fd632e9ffd996df20338f8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz"

SRC_URI[md5sum] = "53b52bf64f0b6b9443428e657b37bc6b"
SRC_URI[sha256sum] = "ed2402caa9434ff5c74b2e7b31178c73e7c7c5c4ea1e1d0e2e39a7dc46958fde"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

