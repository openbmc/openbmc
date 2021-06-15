SUMMARY = "Linux CAN network development utilities"
LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=44;md5=a9e1169c6c9a114a61329e99f86fdd31"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=git"

SRCREV = "eb66451df280f95a9a12e78b151b8d867e1b78ed"

PV = "2020.12.0"

S = "${WORKDIR}/git"

inherit autotools pkgconfig update-alternatives

PACKAGES =+ "${PN}-access ${PN}-isotp ${PN}-j1939 ${PN}-cantest ${PN}-slcan ${PN}-log"

FILES_${PN}-access = " \
    ${bindir}/cangw \
    ${bindir}/canlogserver \
    ${bindir}/bcmserver \
    ${bindir}/socketcand \
    ${bindir}/cannelloni \
"

FILES_${PN}-isotp = "${bindir}/isotp*"

FILES_${PN}-j1939 = " \
    ${bindir}/j* \
    ${bindir}/testj1939 \
"

FILES_${PN}-cantest = " \
    ${bindir}/canbusload \
    ${bindir}/can-calc-bit-timing \
    ${bindir}/canfdtest \
"

FILES_${PN}-slcan = "${bindir}/slcan*"

FILES_${PN}-log = "${bindir}/*log*"

ALTERNATIVE_${PN} = "candump cansend cansequence"
ALTERNATIVE_LINK_NAME[candump] = "${bindir}/candump"
ALTERNATIVE_LINK_NAME[cansend] = "${bindir}/cansend"
ALTERNATIVE_LINK_NAME[cansequence] = "${bindir}/cansequence"
