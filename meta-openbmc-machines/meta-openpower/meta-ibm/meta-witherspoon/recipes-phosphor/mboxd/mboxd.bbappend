MBOXD_FLASH_SIZE = "64M"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN} += "check-pnor-format.service"

SRC_URI += "file://check_pnor_format.sh"

do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/check_pnor_format.sh ${D}${bindir}/check_pnor_format.sh
}
