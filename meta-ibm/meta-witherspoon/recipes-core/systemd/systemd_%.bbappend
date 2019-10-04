FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_ibm-ac-server = " file://journald-storage-policy.conf"
SRC_URI_append_ibm-ac-server = " file://systemd-journald-override.conf"
SRC_URI_append_ibm-ac-server = " file://journald-size-policy.conf"

SRC_URI_append_rainier = " file://journald-storage-policy.conf"
SRC_URI_append_rainier = " file://systemd-journald-override.conf"
SRC_URI_append_rainier = " file://journald-size-policy.conf"

FILES_${PN}_append_ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES_${PN}_append_ibm-ac-server = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES_${PN}_append_ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

FILES_${PN}_append_rainier = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES_${PN}_append_rainier = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES_${PN}_append_rainier = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

do_install_append_ibm-ac-server() {
        install -m 644 -D ${WORKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${WORKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${WORKDIR}/journald-size-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}
do_install_append_rainier() {
        install -m 644 -D ${WORKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${WORKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${WORKDIR}/journald-size-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}
