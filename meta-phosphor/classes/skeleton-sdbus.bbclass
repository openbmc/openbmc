inherit skeleton

DEPENDS += "systemd"

do_install_append() {
        oe_runmake install DESTDIR=${D}
}
