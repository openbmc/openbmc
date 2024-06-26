FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://com.ampere.Hardware.Chassis.Model.MtMitchell.json"

do_install:append() {
	install -m 0644 ${UNPACKDIR}/com.ampere.Hardware.Chassis.Model.MtMitchell.json ${D}${datadir}/phosphor-led-manager/
}
