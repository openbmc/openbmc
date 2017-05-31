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
        ${PN}-fan-control \
        ${PN}-host-state-mgmt \
        ${PN}-inventory \
        ${PN}-leds \
        ${PN}-sensors \
        ${PN}-software \
        ${PN}-host-check-mgmt \
        ${PN}-debug-collector \
        ${PN}-settings \
        "

SUMMARY_${PN}-bmc-state-mgmt = "BMC state management"
RDEPENDS_${PN}-bmc-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-state-manager} \
        "

SUMMARY_${PN}-chassis-state-mgmt = "Chassis state management"
RDEPENDS_${PN}-chassis-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-chassis-state-manager} \
        "

SUMMARY_${PN}-host-check-mgmt = "Host state check on bmc reset"
RDEPENDS_${PN}-host-check-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-check} \
        "

SUMMARY_${PN}-extras = "Extra features"
RDEPENDS_${PN}-extras = " \
        phosphor-rest \
        phosphor-dbus-monitor \
        "

SUMMARY_${PN}-extrasdev = "Development features"
RDEPENDS_${PN}-extrasdev = " \
        rest-dbus \
        "

# Use the fan control package group for applications
# implementing fan control or system fan policy only.
# Applications that create inventory or sensors should
# be added those respective package groups instead.
SUMMARY_${PN}-fan-control = "Fan control"
RDEPENDS_${PN}-fan-control = " \
        ${VIRTUAL-RUNTIME_obmc-fan-control} \
        phosphor-fan-monitor \
        "

SUMMARY_${PN}-host-state-mgmt = "Host state management"
RDEPENDS_${PN}-host-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-state-manager} \
        ${VIRTUAL-RUNTIME_obmc-discover-system-state} \
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
        ${VIRTUAL-RUNTIME_obmc-led-monitor} \
        "

SUMMARY_${PN}-sensors = "Sensor applications"
RDEPENDS_${PN}-sensors = " \
        ${VIRTUAL-RUNTIME_obmc-sensors-hwmon} \
        "

SUMMARY_${PN}-software = "Software applications"
RDEPENDS_${PN}-software = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-code-mgr} \
        ${VIRTUAL-RUNTIME_obmc-bmc-download-mgr} \
        ${VIRTUAL-RUNTIME_obmc-bmc-updater} \
        "
SUMMARY_${PN}-debug-collector = "BMC debug collector"
RDEPENDS_${PN}-debug-collector = " \
        ${VIRTUAL-RUNTIME_obmc-dump-manager} \
        ${VIRTUAL-RUNTIME_obmc-dump-monitor} \
        "

SUMMARY_${PN}-settings = "Settings applications"
RDEPENDS_${PN}-settings = " \
        ${VIRTUAL-RUNTIME_obmc-settings-mgr} \
        "
