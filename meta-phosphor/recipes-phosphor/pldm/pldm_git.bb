SUMMARY = "PLDM Stack"
DESCRIPTION = "Implementation of the PLDM specifications"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson pkgconfig

require pldm.inc

DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "nlohmann-json"
DEPENDS += "boost"
DEPENDS += "cli11"

S = "${WORKDIR}/git"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -Doem-ibm=disabled \
        "
