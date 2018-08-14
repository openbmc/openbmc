SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=890b830b22fd632e9ffd996df20338f8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz"

SRC_URI[md5sum] = "3e65d5b872e441c6a0038191a3dc7ce9"
SRC_URI[sha256sum] = "043756c532dab93e2be33a517ef46b1341f7239278a1045ae670041dd8a4531d"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

