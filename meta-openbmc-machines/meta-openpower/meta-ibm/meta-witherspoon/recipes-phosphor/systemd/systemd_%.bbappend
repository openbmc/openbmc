FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://journald-storage-policy.conf"
SRC_URI += "file://systemd-journald-override.conf"
SRC_URI += "file://journald-size-policy.conf"

FILES_${PN} += "${libdir}/systemd/journald.conf.d/journald-storage-policy.conf"
FILES_${PN} += "${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES_${PN} += "${libdir}/systemd/journald.conf.d/journald-size-policy.conf"

do_install_append() {
        install -m 644 -D ${WORKDIR}/journald-storage-policy.conf ${D}${libdir}/systemd/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${WORKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${WORKDIR}/journald-size-policy.conf ${D}${libdir}/systemd/journald.conf.d/journald-size-policy.conf
}
