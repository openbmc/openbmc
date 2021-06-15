# Common code for dbus applications using python.

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python3-dbus python3-pygobject"
INSTALL_NAME ?= "${PN}"
SCRIPT_NAME ?= "${INSTALL_NAME}.py"

do_install_append() {
        # install the script
        install -d ${D}${sbindir}
        install -m 0755 ${S}/${SCRIPT_NAME} ${D}${sbindir}/${INSTALL_NAME}
}
