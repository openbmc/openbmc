SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
PR = "r1"

inherit autotools pkgconfig pythonnative obmc-phosphor-dbus-service
require phosphor-inventory-manager.inc

DBUS_SERVICE_${PN} = "xyz.openbmc_project.Inventory.Manager.service"
DEPENDS += " \
        ${PN}-config-native \
        sdbusplus \
        sdbusplus-native \
        python-pyyaml-native \
        python-mako-native \
        autoconf-archive-native \
        "
RDEPENDS_${PN} += "sdbusplus"

S = "${WORKDIR}/git"

EXTRA_OECONF = "YAML_PATH=${STAGING_DATADIR_NATIVE}/${PN}"
