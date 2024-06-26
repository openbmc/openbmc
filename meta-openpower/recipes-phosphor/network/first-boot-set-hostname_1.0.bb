SUMMARY = "Init BMC Hostname"
DESCRIPTION = "Setup BMC Unique hostname"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd

RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils}"

SYSTEMD_SERVICE:${PN} = "first-boot-set-hostname.service"

SRC_URI = "file://${BPN}.sh file://${BPN}.service"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
do_install() {
    sed "s/{MACHINE}/${MACHINE}/" -i ${BPN}.sh
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}
    install ${BPN}.sh ${D}${bindir}/
    install -m 644 ${BPN}.service ${D}${systemd_system_unitdir}/
}

