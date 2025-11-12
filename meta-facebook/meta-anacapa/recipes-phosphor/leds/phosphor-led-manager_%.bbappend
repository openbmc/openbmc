# phosphor led configuration
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://led-group-config.json"

do_install:append() {
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}

# Power led is handled by CPLD.
CHASSIS_TARGETS = ""
