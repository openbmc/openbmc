EXTRA_OEMESON:append:gbs = " \
    -Dredfish-dbus-log=enabled \
    -Dhttp-body-limit=40 \
    "

do_install:append:gbs(){
    install -d ${D}${localstatedir}/lib/bmcweb
}
