SUMMARY = "Phosphor OpenBMC Quanta NVME Power Control Service"
DESCRIPTION = "Phosphor OpenBMC Quanta NVME Power Control Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS_append := "${THISDIR}/files:"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "bash"

SRC_URI +=  "file://init_once.sh \
             file://nvme_powermanager.sh \
             file://nvme_powerctrl_library.sh \
             file://nvme_gpio.service \
             file://nvme_powermanager.service \
            "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/init_once.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/nvme_powermanager.sh ${D}${bindir}/

    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/nvme_powerctrl_library.sh ${D}${libexecdir}/

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/nvme_gpio.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/nvme_powermanager.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "nvme_gpio.service nvme_powermanager.service"
