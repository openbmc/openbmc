FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:rpi = " \
    file://xorg.conf.d/98-pitft.conf \
    file://xorg.conf.d/99-calibration.conf \
    file://xorg.conf.d/99-v3d.conf \
"
do_install:append:rpi () {
    install -d ${D}/${sysconfdir}/X11/xorg.conf.d/
    PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
    if [ "${PITFT}" = "1" ]; then
        install -m 0644 ${UNPACKDIR}/xorg.conf.d/98-pitft.conf ${D}/${sysconfdir}/X11/xorg.conf.d/
        install -m 0644 ${UNPACKDIR}/xorg.conf.d/99-calibration.conf ${D}/${sysconfdir}/X11/xorg.conf.d/
    fi
    install -m 0644 ${UNPACKDIR}/xorg.conf.d/99-v3d.conf ${D}/${sysconfdir}/X11/xorg.conf.d/
}

FILES:${PN}:append:rpi = " ${sysconfdir}/X11/xorg.conf.d/*"
