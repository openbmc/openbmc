# Common code for dbus applications using python.

inherit allarch
inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "python-dbus python-pygobject"
SRC_URI += " \
        file://${PN}.py \
        "
S = "${WORKDIR}"

do_install_append() {
        # install the script
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/${PN}.py ${D}${sbindir}/${PN}
}
