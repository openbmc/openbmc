SUMMARY = "Nicole AVSBus control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"
SRC_URI += "file://avsbus-control.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/avsbus-control.sh \
            ${D}${bindir}/avsbus-control.sh
}

TMPL= "avsbus-control@.service"
INSTFMT= "avsbus-control@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
