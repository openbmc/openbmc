FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

FILES:${PN}:append = " \
    ${datadir}/dbus-1/system.d/yosemite4-system.conf \
"

SRC_URI += " \
    file://yosemite4-system.conf \
"

do_install:append() {
    install -d ${D}${datadir}/dbus-1/system.d
    install -m 0644 ${UNPACKDIR}/yosemite4-system.conf ${D}${datadir}/dbus-1/system.d/
}
