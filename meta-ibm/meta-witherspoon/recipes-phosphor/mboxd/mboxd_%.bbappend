MBOXD_FLASH_SIZE_ibm-ac-server = "64M"
MBOXD_FLASH_SIZE_mihawk = "64M"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN}_append_ibm-ac-server = " check-pnor-format.service"

SRC_URI_append_ibm-ac-server = " file://check_pnor_format.sh"

do_install_append_ibm-ac-server() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/check_pnor_format.sh ${D}${bindir}/check_pnor_format.sh
}
