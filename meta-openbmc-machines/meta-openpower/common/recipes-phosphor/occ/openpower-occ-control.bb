SUMMARY = "OpenPOWER OCC controller"
DESCRIPTION = "Application to contol the OpenPOWER On-Chip-Controller"
HOMEPAGE = "https://github.com/openbmc/openpower-occ-control"
PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative

require ${PN}.inc

DBUS_SERVICE_${PN} += "org.open_power.OCC.Control.service"
SYSTEMD_SERVICE_${PN} += "op-occ-disable@.service"

DEPENDS += " \
        sdbusplus \
        sdbusplus-native \
        phosphor-logging \
        openpower-dbus-interfaces \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces-native \
        autoconf-archive-native \
        obmc-targets \
        systemd \
        "

RDEPENDS_${PN} += " \
               sdbusplus \
               phosphor-logging \
               openpower-dbus-interfaces \
               phosphor-dbus-interfaces \
               "

EXTRA_OECONF_append = "${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'i2c-occ', ' --enable-i2c-occ', '', d)}"

# Ensure host stop target requires occ disable service
OCC_DISABLE_SVC = "op-occ-disable@.service"
HOST_STOP_TGTFMT = "obmc-host-stop@{0}.target"
OCC_DISABLE_FMT = "op-occ-disable@{0}.service"
HOST_STOP_FMT = "../${OCC_DISABLE_SVC}:${HOST_STOP_TGTFMT}.requires/${OCC_DISABLE_FMT}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_STOP_FMT', 'OBMC_HOST_INSTANCES')}"

S = "${WORKDIR}/git"
