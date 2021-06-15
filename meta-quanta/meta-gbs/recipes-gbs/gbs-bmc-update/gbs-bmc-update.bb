PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"


inherit systemd
inherit obmc-phosphor-systemd


SRC_URI = " file://phosphor-ipmi-flash-bmc-verify.service \
            file://bmc-verify.sh \
            file://config-bmc.json \
          "

FILES_${PN} += "${datadir}/phosphor-ipmi-flash/config-bmc.json"

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS_${PN} = "bash"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/bmc-verify.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-bmc-verify.service ${D}${systemd_system_unitdir}

    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bmc.json ${D}${datadir}/phosphor-ipmi-flash
}


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "phosphor-ipmi-flash-bmc-verify.service"
