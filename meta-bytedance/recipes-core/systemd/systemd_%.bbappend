do_install_append () {
    rm ${D}${sysconfdir}/systemd/coredump.conf
}
