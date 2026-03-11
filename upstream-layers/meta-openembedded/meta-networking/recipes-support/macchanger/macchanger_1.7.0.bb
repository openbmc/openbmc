SUMMARY = "Tool to view/change network interface MAC addresses"
DESCRIPTION = "A GNU/Linux utility for viewing/manipulating the MAC address of network interfaces."
HOMEPAGE = "https://github.com/alobbs/macchanger"
LICENSE  = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "net"

SRC_URI = "https://github.com/alobbs/macchanger/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Fix-musl-build.patch \
"
SRC_URI[md5sum] = "ca56f16142914337391dac91603eb332"
SRC_URI[sha256sum] = "dae2717c270fd5f62d790dbf80c19793c651b1b26b62c101b82d5fdf25a845bf"

FILES:${PN} = " \
    ${bindir}/${BPN} \
    ${datadir}/${BPN}/wireless.list \
    ${datadir}/${BPN}/OUI.list \
"

FILES:${PN}-doc = " \
    ${datadir}/info \
    ${datadir}/man \
"

inherit autotools
