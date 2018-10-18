SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools \
        pkgconfig \
        pythonnative \
        phosphor-dbus-yaml \
        phosphor-inventory-manager \
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
        libcereal \
        "
RDEPENDS_${PN} += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        "

OBMC_INVENTORY_PATH="${OBMC_DBUS_PATH_ROOT}/inventory"
OBMC_INVENTORY_MGR_IFACE="${OBMC_DBUS_IFACE_ROOT}.Inventory.Manager"

DBUS_SERVICE_${PN} = "${OBMC_INVENTORY_MGR_IFACE}.service"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_NATIVE}${base_datadir} \
        BUSNAME=${OBMC_INVENTORY_MGR_IFACE} \
        INVENTORY_ROOT=${OBMC_INVENTORY_PATH} \
        IFACE=${OBMC_INVENTORY_MGR_IFACE} \
        IFACES_PATH=${STAGING_DIR_NATIVE}${yaml_dir} \
        "
