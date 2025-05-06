HOMEPAGE = "https://github.com/openbmc/pldm"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRC_URI = "git://github.com/openbmc/pldm;branch=master;protocol=https"
SRCREV = "4602326fd45af79ae07efbaf021643a62a695e71"

SUMMARY = "PLDM Stack"
DESCRIPTION = "Implementation of the PLDM specifications"
DEPENDS += "function2"
DEPENDS += "systemd"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"
DEPENDS += "libpldm"
DEPENDS += "phosphor-logging"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "pldmd.service"
SYSTEMD_SERVICE:${PN} += "pldmSoftPowerOff.service"

inherit meson pkgconfig
inherit systemd

PACKAGECONFIG[transport-mctp-demux] = "-Dtransport-implementation=mctp-demux"
PACKAGECONFIG[transport-af-mctp] = "-Dtransport-implementation=af-mctp"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled, -Doem-ibm=disabled, , squashfs-tools"
PACKAGECONFIG[oem-ampere] = "-Doem-ampere=enabled, -Doem-ampere=disabled, libcper"
PACKAGECONFIG[system-specific-bios-json] = "-Dsystem-specific-bios-json=enabled, -Dsystem-specific-bios-json=disabled"
PACKAGECONFIG ??= ""
PACKAGECONFIG:append:df-mctp = " transport-af-mctp"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

pkg_prerm:${PN} () {
    LINK="$D$systemd_system_unitdir/obmc-host-shutdown@0.target.wants/pldmSoftPowerOff.service"
    rm $LINK
    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.wants/pldmSoftPowerOff.service"
    rm $LINK
}
# Install pldmSoftPowerOff.service in correct targets
pkg_postinst:${PN} () {
    mkdir -p $D$systemd_system_unitdir/obmc-host-shutdown@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-shutdown@0.target.wants/pldmSoftPowerOff.service"
    TARGET="../pldmSoftPowerOff.service"
    ln -s $TARGET $LINK
    mkdir -p $D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.wants/pldmSoftPowerOff.service"
    TARGET="../pldmSoftPowerOff.service"
    ln -s $TARGET $LINK
}
