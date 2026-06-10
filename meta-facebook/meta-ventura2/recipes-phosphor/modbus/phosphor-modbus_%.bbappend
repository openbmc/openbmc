FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://allowed-devices.json"

do_install:append() {
    install -d ${D}/var/lib/phosphor-modbus
    install -m 0644 ${UNPACKDIR}/allowed-devices.json ${D}/var/lib/phosphor-modbus/
}
