HOMEPAGE = "https://github.com/openbmc/pldm"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRC_URI = "git://github.com/openbmc/pldm;branch=master;protocol=https"
SRCREV = "d310f8213a41b29c8e36e67c1a9570b72deaa958"

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

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -Doem-ibm=disabled \
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
