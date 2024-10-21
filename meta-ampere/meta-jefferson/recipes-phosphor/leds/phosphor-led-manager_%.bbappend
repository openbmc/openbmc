FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://com.ampere.Hardware.Chassis.Model.MtJefferson.json"

do_install:append() {
	install -m 0644 ${WORKDIR}/com.ampere.Hardware.Chassis.Model.MtJefferson.json ${D}${datadir}/phosphor-led-manager/
}
