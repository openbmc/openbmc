# Common code for dbus applications using c.

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

DEPENDS += "glib-2.0"
SRC_URI += " \
        file://Makefile \
        file://${PN}.c \
        "
S = "${WORKDIR}"

do_install_append() {
        # install the binary
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/${PN} ${D}${sbindir}
}
