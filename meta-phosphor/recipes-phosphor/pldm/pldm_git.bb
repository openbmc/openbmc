SUMMARY = "PLDM Stack"
DESCRIPTION = "Implementation of the PLDM specifications"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson pkgconfig
inherit systemd

require pldm.inc

DEPENDS += "systemd"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "pldmd.service"
SYSTEMD_SERVICE_${PN} += "pldmSoftPowerOff.service"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -Doem-ibm=disabled \
        "
