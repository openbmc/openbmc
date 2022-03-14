SUMMARY = "canutils (PTX flavour)"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "console/network"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsocketcan"

SRCREV = "299dff7f5322bf0348dcdd60071958ebedf5f09d"
SRC_URI = "git://git.pengutronix.de/git/tools/canutils.git;protocol=git;branch=master \
    file://0001-canutils-candump-Add-error-frame-s-handling.patch \
"

inherit update-alternatives

S = "${WORKDIR}/git"

inherit autotools pkgconfig

# Busybox ip doesn't support can interface configuration, use the real thing
RDEPENDS:${PN} += "iproute2"

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN} = "candump cansend cansequence"
ALTERNATIVE_LINK_NAME[candump] = "${bindir}/candump"
ALTERNATIVE_LINK_NAME[cansend] = "${bindir}/cansend"
ALTERNATIVE_LINK_NAME[cansequence] = "${bindir}/cansequence"
