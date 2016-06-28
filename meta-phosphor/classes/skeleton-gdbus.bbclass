inherit skeleton

DEPENDS += "glib-2.0 obmc-libobmc-intf"

do_install_append() {
        oe_runmake install DESTDIR=${D}
}
