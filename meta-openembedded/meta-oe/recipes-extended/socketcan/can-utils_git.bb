SUMMARY = "Linux CAN network development utilities"
LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=44;md5=a9e1169c6c9a114a61329e99f86fdd31"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=git"

SRCREV = "eb66451df280f95a9a12e78b151b8d867e1b78ed"

PV = "2020.12.0"

S = "${WORKDIR}/git"

inherit autotools pkgconfig update-alternatives

ALTERNATIVE_${PN} = "candump cansend cansequence"
ALTERNATIVE_LINK_NAME[candump] = "${bindir}/candump"
ALTERNATIVE_LINK_NAME[cansend] = "${bindir}/cansend"
ALTERNATIVE_LINK_NAME[cansequence] = "${bindir}/cansequence"
