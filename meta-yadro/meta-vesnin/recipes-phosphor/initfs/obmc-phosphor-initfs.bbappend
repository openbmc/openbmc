RDEPENDS_${PN}_remove += " cf-fsi-firmware "

do_install_append() {
    echo "/etc/hostname" >> ${D}/whitelist
}
