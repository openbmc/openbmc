SUMMARY = "Phosphor IPMI daemon configuration"
PR = "r1"

inherit obmc-phosphor-license
inherit allarch

SRC_URI = " \
    file://cipher_list.json \
    file://dcmi_cap.json \
    file://dcmi_sensors.json \
    file://dev_id.json \
    file://power_reading.json \
    "

FILES_${PN} = " \
    ${datadir}/ipmi-providers/cipher_list.json \
    ${datadir}/ipmi-providers/dcmi_cap.json \
    ${datadir}/ipmi-providers/dcmi_sensors.json \
    ${datadir}/ipmi-providers/dev_id.json \
    ${datadir}/ipmi-providers/power_reading.json \
    "

do_fetch[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/ipmi-providers
    install -m 0644 -D ${WORKDIR}/cipher_list.json \
        ${D}${datadir}/ipmi-providers/cipher_list.json
    install -m 0644 -D ${WORKDIR}/dcmi_cap.json \
        ${D}${datadir}/ipmi-providers/dcmi_cap.json
    install -m 0644 -D ${WORKDIR}/dcmi_sensors.json \
        ${D}${datadir}/ipmi-providers/dcmi_sensors.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
    install -m 0644 -D ${WORKDIR}/power_reading.json \
        ${D}${datadir}/ipmi-providers/power_reading.json
}
