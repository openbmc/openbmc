SUMMARY = "OpenBMC - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-extras \
        ${PN}-extrasdev \
        ${PN}-inventory \
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
        "
