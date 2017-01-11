SUMMARY = "OpenBMC - Sensors"

PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-providers"

SUMMARY_${PN}-providers = "Providers of sensor objects"
RDEPENDS_${PN}-providers = " \
        virtual-obmc-sensors-hwmon \
        "
