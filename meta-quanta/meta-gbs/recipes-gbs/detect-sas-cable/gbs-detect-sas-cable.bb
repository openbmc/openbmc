SUMMARY = "OpenBMC Quanta Detect SAS Cable Service"
DESCRIPTION = "OpenBMC Quanta Detect SAS Cable Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

SRC_URI =  " file://detect-sas-cable.sh \
             file://detect-sas-cable.service \
            "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/detect-sas-cable.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/detect-sas-cable.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "detect-sas-cable.service"
