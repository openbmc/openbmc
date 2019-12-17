MBOXD_FLASH_SIZE_ibm-ac-server = "64M"
MBOXD_FLASH_SIZE_mihawk = "64M"
MBOXD_FLASH_SIZE_rainier = "64M"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN}_append_df-openpower-ubi-fs = " check-pnor-format.service"

SRC_URI_append_df-openpower-ubi-fs = " file://check_pnor_format.sh"

do_install_append_df-openpower-ubi-fs() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/check_pnor_format.sh ${D}${bindir}/check_pnor_format.sh
}
