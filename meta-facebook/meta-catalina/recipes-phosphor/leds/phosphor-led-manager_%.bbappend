FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " file://led-group-config.json"

# Power led is handled by CPLD.
CHASSIS_TARGETS = ""

do_install:append() {
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
