# phosphor led configuration
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " file://led-group-config.json"

# On Yv5, the power LED is controlled by the CPLD.
CHASSIS_TARGETS = ""

do_install:append() {
    install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
