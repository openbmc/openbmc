SUMMARY = "centriq2400 VR sensor Re-Probe"
DESCRIPTION = "IR38163 work on DC on state, need re-probe for centriq2400 HW design"
PR = "r0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

PROVIDES += 'virtual/ir38163-workaround'
RPROVIDES_${PN} += 'virtual-ir38163-workaround'

IR_REMOVE = "ir-remove@.service"
IR_ADD = "ir-add@.service"
INSTIR_REMOVE = "ir-remove@{0}.service"
INSTIR_ADD = "ir-add@{0}.service"
TGTFMT_OFF = "obmc-chassis-poweroff@{0}.target"
TGTFMT_ON = "obmc-chassis-poweron@{0}.target"
FMT_REMOVE = "../${IR_REMOVE}:${TGTFMT_OFF}.wants/${INSTIR_REMOVE}"
FMT_ADD = "../${IR_ADD}:${TGTFMT_ON}.requires/${INSTIR_ADD}"

SYSTEMD_SERVICE_${PN} += "${IR_REMOVE}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_REMOVE', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${IR_ADD}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ADD', 'OBMC_CHASSIS_INSTANCES')}"

SRC_URI += "file://ir38163_workaround.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/ir38163_workaround.sh ${D}${bindir}/ir38163_workaround.sh
}
