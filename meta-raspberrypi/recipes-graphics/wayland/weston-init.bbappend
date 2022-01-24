FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

do_install:append:rpi() {
    if [ -e ${D}/${sysconfdir}/init.d/weston ]; then
        sed -i 's#weston-start --#weston-start -- --continue-without-input#' ${D}/${sysconfdir}/init.d/weston
    fi
    if [ -e ${D}${systemd_system_unitdir}/weston.service ]; then
        sed -i 's#ExecStart=/usr/bin/weston#ExecStart=/usr/bin/weston --continue-without-input#' ${D}${systemd_system_unitdir}/weston.service
    fi
}
