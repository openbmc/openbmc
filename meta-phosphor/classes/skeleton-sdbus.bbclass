inherit skeleton

DEPENDS_append_class-target = " systemd"

do_compile_class-native() {
    :
}

do_install_append_class-target() {
        oe_runmake install DESTDIR=${D}
}
