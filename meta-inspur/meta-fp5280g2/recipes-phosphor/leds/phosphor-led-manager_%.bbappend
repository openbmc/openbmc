FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://led-group-config.json"

PACKAGECONFIG_append = " use-json"

DEPENDS += "phosphor-dbus-interfaces"

do_install_append() {
        install -d ${D}${datadir}/phosphor-led-manager/
        install -m 0644 ${WORKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
