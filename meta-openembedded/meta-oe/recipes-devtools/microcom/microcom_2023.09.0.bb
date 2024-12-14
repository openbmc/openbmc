SUMMARY = "Minimalistic terminal program for communicating with devices over a serial connection"
HOMEPAGE = "https://github.com/pengutronix/microcom"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9f7c009791eaa4b9ca90dc4c9538d24"

SRC_URI = "https://github.com/pengutronix/microcom/releases/download/v${PV}/microcom-${PV}.tar.xz"
SRC_URI[sha256sum] = "ef42184bb35c9762b3e9c70748696f7478efacad8412a88aaf2d9a6a500231a1"

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
