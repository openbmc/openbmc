SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
PR = "r1"

inherit autotools \
        pkgconfig \
        pythonnative \
        obmc-phosphor-dbus-service

require phosphor-inventory-manager.inc

DEPENDS += " \
        ${PN}-config-native \
        sdbusplus \
        sdbusplus-native \
        autoconf-archive-native \
        "
RDEPENDS_${PN} += "sdbusplus"

OBMC_INVENTORY_PATH="${OBMC_DBUS_PATH_ROOT}/Inventory"
OBMC_INVENTORY_MGR_IFACE="${OBMC_DBUS_IFACE_ROOT}.Inventory.Manager"

DBUS_SERVICE_${PN} = "${OBMC_INVENTORY_MGR_IFACE}.service"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DATADIR_NATIVE}/${PN} \
        BUSNAME=${OBMC_INVENTORY_MGR_IFACE} \
        INVENTORY_ROOT=${OBMC_INVENTORY_PATH} \
        IFACE=${OBMC_INVENTORY_MGR_IFACE} \
        "
