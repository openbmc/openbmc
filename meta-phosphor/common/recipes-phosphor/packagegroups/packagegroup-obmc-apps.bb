SUMMARY = "OpenBMC - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-bmc-state-mgmt \
        ${PN}-chassis-state-mgmt \
        ${PN}-extras \
        ${PN}-extrasdev \
        ${PN}-host-state-mgmt \
        ${PN}-inventory \
        ${PN}-leds \
        ${PN}-sensors \
        ${PN}-software \
        "

SUMMARY_${PN}-bmc-state-mgmt = "BMC state management"
RDEPENDS_${PN}-bmc-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-state-manager} \
        "

SUMMARY_${PN}-chassis-state-mgmt = "Chassis state management"
RDEPENDS_${PN}-chassis-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-chassis-state-manager} \
        "

SUMMARY_${PN}-extras = "Extra features"
RDEPENDS_${PN}-extras = " \
        phosphor-rest \
        "

SUMMARY_${PN}-extrasdev = "Development features"
RDEPENDS_${PN}-extrasdev = " \
        rest-dbus \
        "

SUMMARY_${PN}-host-state-mgmt = "Host state management"
RDEPENDS_${PN}-host-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-state-manager} \
        "

SUMMARY_${PN}-inventory = "Inventory applications"
RDEPENDS_${PN}-inventory = " \
        ${VIRTUAL-RUNTIME_obmc-inventory-manager} \
        ${VIRTUAL-RUNTIME_obmc-fan-presence} \
        "

SUMMARY_${PN}-leds = "LED applications"
RDEPENDS_${PN}-leds = " \
        ${VIRTUAL-RUNTIME_obmc-leds-manager} \
        ${VIRTUAL-RUNTIME_obmc-leds-sysfs} \
        "

SUMMARY_${PN}-sensors = "Sensor applications"
RDEPENDS_${PN}-sensors = " \
        ${VIRTUAL-RUNTIME_obmc-sensors-hwmon} \
        "

SUMMARY_${PN}-software = "Software applications"
RDEPENDS_${PN}-software = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-code-mgr} \
        "
