do_install_append() {
        sed -i -e '$anss_initgroups_ignoreusers ALLLOCAL' ${D}${sysconfdir}/nslcd.conf

        install -m 0644 ${D}${sysconfdir}/nslcd.conf ${D}${sysconfdir}/nslcd.conf.default
}
