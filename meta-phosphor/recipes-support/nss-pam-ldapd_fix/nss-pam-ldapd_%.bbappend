do_install_append() {
        install -m 0644 ${D}${sysconfdir}/nslcd.conf ${D}${sysconfdir}/nslcd.conf.default
}
