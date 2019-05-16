FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://10-nice.conf"

inherit systemd

FILES_${PN} += "${systemd_unitdir}/system/rngd.service.d"

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system/rngd.service.d
        install -m 644 ${WORKDIR}/10-nice.conf ${D}${systemd_unitdir}/system/rngd.service.d
    fi
}
