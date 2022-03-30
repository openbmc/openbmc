SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz"

SRC_URI[sha256sum] = "cb67b6491a618867fc4f9848f586019f1bb2ebd149f393afac5544ee55e4544f"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

