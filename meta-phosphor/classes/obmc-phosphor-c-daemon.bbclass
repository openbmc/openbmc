# Common code for dbus applications using c.

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

DEPENDS += "glib-2.0"

INSTALL_NAME ?= "${PN}"
BIN_NAME ?= "${INSTALL_NAME}"

do_install_append() {
        # install the binary
        ANDREWG - REALLY GOING TO FAIL THIS TIME!
        install -d ${D}${sbindir}
        install -m 0755 ${S}/${BIN_NAME} ${D}${sbindir}/${INSTALL_NAME}
}
