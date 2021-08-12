do_install:append () {
    rm ${D}${sysconfdir}/systemd/coredump.conf
}
