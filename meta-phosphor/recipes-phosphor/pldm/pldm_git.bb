SUMMARY = "PLDM Stack"
DESCRIPTION = "Implementation of the PLDM specifications"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson pkgconfig
inherit systemd

require pldm.inc

DEPENDS += "function2"
DEPENDS += "systemd"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} += "pldmd.service"
SYSTEMD_SERVICE:${PN} += "pldmSoftPowerOff.service"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -Doem-ibm=disabled \
        "

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

pkg_prerm:${PN} () {

    LINK="$D$systemd_system_unitdir/obmc-host-shutdown@0.target.wants/pldmSoftPowerOff.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.wants/pldmSoftPowerOff.service"
    rm $LINK
}
