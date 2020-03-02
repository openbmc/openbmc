FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://10-nice.conf"

PACKAGECONFIG_remove = "\
        ${@bb.utils.contains('MACHINE_FEATURES', 'hw-rng', \
                             'libjitterentropy', '', d)}\
        "

inherit systemd

FILES_${PN} += "${systemd_unitdir}/system/rngd.service.d"

do_install_append() {

    # When using systemd and using libjitterentropy, install a config
    # which runs rngd at a 'nice' priority.  libjitterentropy uses a
    # lot of CPU early on in the boot process and makes the whole boot
    # go slower.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        if ${@bb.utils.contains('PACKAGECONFIG', 'libjitterentropy', 'true', 'false', d)}; then
            install -d ${D}${systemd_unitdir}/system/rngd.service.d
            install -m 644 ${WORKDIR}/10-nice.conf \
                ${D}${systemd_unitdir}/system/rngd.service.d
        fi
    fi
}
