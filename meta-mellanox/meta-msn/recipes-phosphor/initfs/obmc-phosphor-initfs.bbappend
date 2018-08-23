FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-update_all.sh"
SRC_URI += "file://failsafe-boot.sh"
SRC_URI += "file://recovery.sh"


do_install_append() {
        install -m 0755 ${WORKDIR}/obmc-init.sh ${D}/init
        install -m 0755 ${WORKDIR}/failsafe-boot.sh ${D}/failsafe
        install -m 0755 ${WORKDIR}/obmc-update_all.sh ${D}/update_all
        install -m 0755 ${WORKDIR}/recovery.sh ${D}/recovery
}

FILES_${PN} += " /update_all /failsafe /recovery"
