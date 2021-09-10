inherit skeleton

DEPENDS:append:class-target = " glib-2.0 obmc-libobmc-intf"

do_compile:class-native() {
    :
}

do_install:append:class-target() {
        oe_runmake install DESTDIR=${D}
}
