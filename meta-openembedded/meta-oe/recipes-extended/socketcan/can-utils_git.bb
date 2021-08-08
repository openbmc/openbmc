SUMMARY = "Linux CAN network development utilities"
LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=44;md5=a9e1169c6c9a114a61329e99f86fdd31"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=git"

SRCREV = "e9dd86fa5c4e6ecdfc34e487634a32f19e5c4d63"

PV = "2021.06.0"

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
