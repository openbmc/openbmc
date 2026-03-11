SUMMARY = "Linux CAN network development utilities"
LICENSE = "GPL-2.0-only & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=44;md5=a9e1169c6c9a114a61329e99f86fdd31"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=https;branch=master \
           file://0001-Include-time.h-for-timespec-struct-definition.patch"

SRCREV = "01083a64ebf28cc716efe2d2fd51c141042ae34b"


inherit cmake pkgconfig update-alternatives

PACKAGES =+ " \
    ${PN}-access \
    ${PN}-cantest \
    ${PN}-isobusfs \
    ${PN}-isobusfs-dev \
    ${PN}-isotp \
    ${PN}-j1939 \
    ${PN}-log \
    ${PN}-mcp251xfd \
    ${PN}-slcan \
"

FILES:${PN}-access = " \
    ${bindir}/cangw \
    ${bindir}/canlogserver \
    ${bindir}/bcmserver \
"

FILES:${PN}-cantest = " \
    ${bindir}/canbusload \
    ${bindir}/can-calc-bit-timing \
    ${bindir}/canfdtest \
"

FILES:${PN}-isobusfs = " \
    ${bindir}/isobusfs-cli \
    ${bindir}/isobusfs-srv \
    ${libdir}/libisobusfs.so \
"

FILES:${PN}-isobusfs-dev = " \
    ${includedir}/isobusfs* \
"

FILES:${PN}-isotp = "${bindir}/isotp*"

FILES:${PN}-j1939 = " \
    ${bindir}/j* \
    ${bindir}/testj1939 \
"

FILES:${PN}-log = "${bindir}/*log*"

FILES:${PN}-mcp251xfd = " \
    ${bindir}/mcp251xfd* \
"

FILES:${PN}-slcan = "${bindir}/slcan*"

ALTERNATIVE:${PN} = "candump cansend cansequence"
ALTERNATIVE_LINK_NAME[candump] = "${bindir}/candump"
ALTERNATIVE_LINK_NAME[cansend] = "${bindir}/cansend"
ALTERNATIVE_LINK_NAME[cansequence] = "${bindir}/cansequence"

# busybox ip fails to configure can interfaces, so we need iproute2 to do so.
# See details in http://www.armadeus.com/wiki/index.php?title=CAN_bus_Linux_driver.
RRECOMMENDS:${PN} += "iproute2-ip"
