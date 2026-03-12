SUMMARY = "Minimalistic terminal program for communicating with devices over a serial connection"
HOMEPAGE = "https://github.com/pengutronix/microcom"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9f7c009791eaa4b9ca90dc4c9538d24"

SRC_URI = "https://github.com/pengutronix/microcom/releases/download/v${PV}/microcom-${PV}.tar.xz"
SRC_URI[sha256sum] = "b1d734a249d8613db7ca1f1bb2ec4e28a35234a60212d91e0c00cc22e9c67a39"

UPSTREAM_CHECK_URI = "${HOMEPAGE}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

DEPENDS = "readline"

inherit autotools update-alternatives

PACKAGECONFIG ??= ""
PACKAGECONFIG[can] = "--enable-can,--disable-can"

EXTRA_OECONF = "--enable-largefile"

# higher priority than busybox' microcom
ALTERNATIVE:${PN} = "microcom"
ALTERNATIVE_PRIORITY[microcom] = "100"
