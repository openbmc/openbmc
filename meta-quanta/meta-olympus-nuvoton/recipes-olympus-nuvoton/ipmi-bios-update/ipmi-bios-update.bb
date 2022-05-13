PR = "r1"
LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
                    file://${COREBASE}/meta/files/common-licenses/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
                   "
inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "bash"

SRC_URI += " file://bios-verify.sh \
             file://bios-update.sh \
             file://phosphor-ipmi-flash-bios-verify.service \
             file://phosphor-ipmi-flash-bios-update.service \
             file://config-bios.json \
           "

FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-bios.json"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/bios-verify.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/bios-update.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-bios-verify.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-bios-update.service ${D}${systemd_system_unitdir}

    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bios.json ${D}${datadir}/phosphor-ipmi-flash
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "phosphor-ipmi-flash-bios-verify.service phosphor-ipmi-flash-bios-update.service"
