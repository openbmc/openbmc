SUMMARY = "Linux CAN network development utilities"
LICENSE = "GPL-2.0-only & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=44;md5=a9e1169c6c9a114a61329e99f86fdd31"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=https;branch=master"

SRCREV = "cfe41963f3425e9adb01a70cfaddedf5e5982720"

S = "${WORKDIR}/git"

inherit autotools pkgconfig update-alternatives

PACKAGES =+ "${PN}-access ${PN}-isotp ${PN}-j1939 ${PN}-cantest ${PN}-slcan ${PN}-log"

FILES:${PN}-access = " \
    ${bindir}/cangw \
    ${bindir}/canlogserver \
    ${bindir}/bcmserver \
    ${bindir}/socketcand \
    ${bindir}/cannelloni \
"

FILES:${PN}-isotp = "${bindir}/isotp*"

FILES:${PN}-j1939 = " \
    ${bindir}/j* \
    ${bindir}/testj1939 \
"

FILES:${PN}-cantest = " \
    ${bindir}/canbusload \
    ${bindir}/can-calc-bit-timing \
    ${bindir}/canfdtest \
"

FILES:${PN}-slcan = "${bindir}/slcan*"

FILES:${PN}-log = "${bindir}/*log*"

ALTERNATIVE:${PN} = "candump cansend cansequence"
ALTERNATIVE_LINK_NAME[candump] = "${bindir}/candump"
ALTERNATIVE_LINK_NAME[cansend] = "${bindir}/cansend"
ALTERNATIVE_LINK_NAME[cansequence] = "${bindir}/cansequence"

# busybox ip fails to configure can interfaces, so we need iproute2 to do so.
# See details in http://www.armadeus.com/wiki/index.php?title=CAN_bus_Linux_driver.
RRECOMMENDS:${PN} += "iproute2"

