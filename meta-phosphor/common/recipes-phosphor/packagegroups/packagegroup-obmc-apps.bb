SUMMARY = "OpenBMC - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-extras \
        ${PN}-extrasdev \
        ${PN}-inventory \
        ${PN}-sensors \
        "

SUMMARY_${PN}-extras = "Extra features"
RDEPENDS_${PN}-extras = " \
        phosphor-rest \
        "

SUMMARY_${PN}-extrasdev = "Development features"
RDEPENDS_${PN}-extrasdev = " \
        rest-dbus \
        "

SUMMARY_${PN}-inventory = "Inventory support"
RDEPENDS_${PN}-inventory = " \
        phosphor-inventory-manager \
        "

SUMMARY_${PN}-sensors = "Sensor support"
RDEPENDS_${PN}-sensors = " \
        virtual-obmc-sensors-hwmon \
        "
