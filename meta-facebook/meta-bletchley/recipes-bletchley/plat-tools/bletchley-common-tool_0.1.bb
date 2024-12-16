LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += " bash motor-ctrl"
RDEPENDS:${PN} += " mdio-tools"

SRC_URI += " \
    file://bletchley-system-state-init \
    file://bletchley-system-state-init@.service \
    file://bletchley-usbmux-util \
    file://bletchley-net-util \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-system-state-init ${D}${libexecdir}

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/bletchley-usbmux-util ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/bletchley-net-util ${D}${bindir}
}


TGT = "${SYSTEMD_DEFAULT_TARGET}"
BLETCHLEY_SYS_ST_INIT_INSTFMT="../bletchley-system-state-init@.service:${TGT}.wants/bletchley-system-state-init@{0}.service"

SYSTEMD_SERVICE:${PN} += "bletchley-system-state-init@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'BLETCHLEY_SYS_ST_INIT_INSTFMT', 'OBMC_HOST_INSTANCES')}"
