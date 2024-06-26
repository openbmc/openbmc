MBOXD_FLASH_SIZE:ibm-ac-server = "64M"
MBOXD_FLASH_SIZE:p10bmc = "64M"
MBOXD_WINDOW_NUM:p10bmc = "63"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN}:append:df-openpower-ubi-fs = " check-pnor-format.service"

SRC_URI:append:df-openpower-ubi-fs = " file://check_pnor_format.sh"

do_install:append:df-openpower-ubi-fs() {
	install -d ${D}${bindir}
	install -m 0755 ${UNPACKDIR}/check_pnor_format.sh ${D}${bindir}/check_pnor_format.sh
}
