do_install:append() {
    sed -i "s/--user usbmux//" ${D}${systemd_system_unitdir}/usbmuxd.service
    sed -i "s#/var/run#/run#" ${D}${systemd_system_unitdir}/usbmuxd.service
}

