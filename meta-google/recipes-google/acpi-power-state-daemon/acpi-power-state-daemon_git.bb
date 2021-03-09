SUMMARY = "ACPI Power/Sleep state daemon to allow host state events"
DESCRIPTION = "ACPI Power/Sleep state daemon to allow host state events"
HOMEPAGE = "http://github.com/openbmc/google-misc"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "git://github.com/openbmc/google-misc"
SRCREV = "15fe169d1fbdd510bb9cfc9bb725baf0613350ff"
S = "${WORKDIR}/git/acpi-power-state-daemon"

inherit meson
inherit pkgconfig
inherit systemd

DEPENDS += " \
    phosphor-dbus-interfaces \
    sdbusplus \
    systemd \
    "

SYSTEMD_SERVICE_${PN} = " \
    acpi-power-state.service \
    host-s0-state.target \
    host-s5-state.target \
    "
