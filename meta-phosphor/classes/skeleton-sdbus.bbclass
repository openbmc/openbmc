inherit skeleton

DEPENDS:append:class-target = " systemd"

do_compile:class-native() {
    :
}

do_install:append:class-target() {
        oe_runmake install DESTDIR=${D}
}
