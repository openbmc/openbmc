SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools \
        pkgconfig \
        python3native \
        phosphor-dbus-yaml \
        phosphor-inventory-manager \
        obmc-phosphor-dbus-service

require phosphor-inventory-manager.inc

DEPENDS += " \
        phosphor-inventory-manager-assettag \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        ${PYTHON_PN}-sdbus++-native \
        autoconf-archive-native \
        libcereal \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-mako-native \
        "

OBMC_INVENTORY_PATH="${OBMC_DBUS_PATH_ROOT}/inventory"
OBMC_INVENTORY_MGR_IFACE="${OBMC_DBUS_IFACE_ROOT}.Inventory.Manager"

DBUS_SERVICE_${PN} = "${OBMC_INVENTORY_MGR_IFACE}.service"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_HOST}${base_datadir} \
        BUSNAME=${OBMC_INVENTORY_MGR_IFACE} \
        INVENTORY_ROOT=${OBMC_INVENTORY_PATH} \
        IFACE=${OBMC_INVENTORY_MGR_IFACE} \
        IFACES_PATH=${STAGING_DIR_TARGET}${yaml_dir} \
        "

PACKAGECONFIG ??= ""
PACKAGECONFIG[associations] = "--enable-associations, --disable-associations,nlohmann-json,"
