FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EEPROMS = "board motherboard exp hdd"

SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list_zip(d, 'obmc/eeproms/[0]', 'EEPROMS')}"
SYSTEMD_GENLINKS_${PN} += "../obmc-read-eeprom@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-read-eeprom@obmc-eeproms-[0].service:EEPROMS"
SYSTEMD_GENOVERRIDES_${PN} += "obmc-vpd-deps.conf:obmc-read-eeprom@obmc-eeproms-[0].service.d/obmc-vpd-deps.conf:EEPROMS"

