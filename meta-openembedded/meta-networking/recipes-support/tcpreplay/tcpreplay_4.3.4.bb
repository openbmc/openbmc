SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz"

SRC_URI[md5sum] = "305a84d84265705bd28f148698142188"
SRC_URI[sha256sum] = "ee065310806c22e2fd36f014e1ebb331b98a7ec4db958e91c3d9cbda0640d92c"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

