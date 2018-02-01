SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=890b830b22fd632e9ffd996df20338f8"

SRC_URI = "http://prdownloads.sourceforge.net/tcpreplay/${PV}/tcpreplay-${PV}.tar.gz"

SRC_URI[md5sum] = "3105b10b07dbc0b07ce2da07a2368359"
SRC_URI[sha256sum] = "da483347e83a9b5df0e0dbb0f822a2d37236e79dda35f4bc4e6684fa827f25ea"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

