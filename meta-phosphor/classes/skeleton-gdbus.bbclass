inherit skeleton

DEPENDS_append_class-target = " glib-2.0 obmc-libobmc-intf"

do_compile_class-native() {
    :
}

do_install_append_class-target() {
        oe_runmake install DESTDIR=${D}
}
