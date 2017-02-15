SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
PR = "r1"

inherit autotools \
        pkgconfig \
        pythonnative \
        phosphor-dbus-interfaces \
        phosphor-inventory-manager \
        obmc-dbus \
        obmc-phosphor-dbus-service

require phosphor-inventory-manager.inc

DEPENDS += " \
        ${PN}-config-native \
        phosphor-dbus-interfaces \
        phosphor-dbus-interfaces-native \
        phosphor-logging \
        sdbusplus \
        sdbusplus-native \
        autoconf-archive-native \
        "
RDEPENDS_${PN} += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        "

DBUS_SERVICE_${PN} = "${obmc_inventory_manager_iface}.service"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_NATIVE}${base_datadir} \
        BUSNAME=${obmc_inventory_manager_iface} \
        INVENTORY_ROOT=${obmc_inventory_root} \
        IFACE=${obmc_inventory_manager_iface} \
        IFACES_PATH=${STAGING_DIR_NATIVE}${yaml_dir} \
        MAPPER_PATH=${obmc_mapper_path} \
        MAPPER_BUSNAME=${obmc_mapper_busname} \
        MAPPER_IFACE=${obmc_mapper_query_iface} \
        "
