SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager is an inventory object \
lifecycle management application, suitable for use on a wide variety \
of OpenBMC platforms."
DEPENDS += " \
        phosphor-inventory-manager-assettag \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        ${PYTHON_PN}-sdbus++-native \
        libcereal \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-mako-native \
        nlohmann-json \
        "
PACKAGECONFIG ??= ""
PACKAGECONFIG[associations] = "-Dassociations=enabled, -Dassociations=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit meson \
        pkgconfig \
        python3native \
        phosphor-dbus-yaml \
        phosphor-inventory-manager \
        obmc-phosphor-dbus-service

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -DYAML_PATH=${STAGING_DIR_HOST}${base_datadir} \
        -DIFACES_PATH=${STAGING_DIR_TARGET}${yaml_dir} \
        "

require phosphor-inventory-manager.inc

OBMC_INVENTORY_PATH = "${OBMC_DBUS_PATH_ROOT}/inventory"
OBMC_INVENTORY_MGR_IFACE = "${OBMC_DBUS_IFACE_ROOT}.Inventory.Manager"
DBUS_SERVICE:${PN} = "${OBMC_INVENTORY_MGR_IFACE}.service"
