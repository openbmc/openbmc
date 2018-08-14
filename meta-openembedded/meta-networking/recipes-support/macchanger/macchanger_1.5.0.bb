SUMMARY = "Tool to view/change network interface MAC addresses"
DESCRIPTION = "A GNU/Linux utility for viewing/manipulating the MAC address of network interfaces."
HOMEPAGE = "http://www.alobbs.com/macchanger"
LICENSE  = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
SECTION = "net"

SRC_URI  = "${GNU_MIRROR}/macchanger/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "79b7cdaeca3d8ebafa764c4b0dd03ab7"
SRC_URI[sha256sum] = "d44bfa27cb29c5a718627cb3ef3aa42eb5130426545eb2031120826cd73fa8fe"

FILES_${PN} = " \
    ${bindir}/${BPN} \
    ${datadir}/${BPN}/wireless.list \
    ${datadir}/${BPN}/OUI.list \
"

FILES_${PN}-doc = " \
    ${datadir}/info \
    ${datadir}/man \
"

inherit autotools
