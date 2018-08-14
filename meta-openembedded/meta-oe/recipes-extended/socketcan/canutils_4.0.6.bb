SUMMARY = "canutils (PTX flavour)"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "console/network"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsocketcan"

SRCREV = "299dff7f5322bf0348dcdd60071958ebedf5f09d"
SRC_URI = "git://git.pengutronix.de/git/tools/canutils.git;protocol=git \
    file://0001-canutils-candump-Add-error-frame-s-handling.patch \
"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

# Busybox ip doesn't support can interface configuration, use the real thing
RDEPENDS_${PN} += "iproute2"
