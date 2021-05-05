EXTRA_OEMESON_append_gbs = " \
    -Dredfish-dbus-log=enabled \
    -Dhttp-body-limit=40 \
    "

do_install_append_gbs(){
    install -d ${D}${localstatedir}/lib/bmcweb
}
