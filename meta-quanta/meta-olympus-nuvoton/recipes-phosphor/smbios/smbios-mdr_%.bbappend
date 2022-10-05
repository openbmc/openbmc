FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"
SRCREV:olympus-nuvoton = "7393e48def95e9a9db400f0b0293e3ea088bb1a4"

SRC_URI:append:olympus-nuvoton = " file://smbios2"
SRC_URI:append:olympus-nuvoton = " file://smbios-mdrv2.service"
SRC_URI:append:olympus-nuvoton = " file://xyz.openbmc_project.cpuinfo.service"
SRC_URI:append:olympus-nuvoton = " file://default-smbios.service"

FILES:${PN}:append:olympus-nuvoton = " ${datadir}/smbios/smbios2"

SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " default-smbios.service"

do_install:append:olympus-nuvoton() {
    # For default smbios service install to var
    install -d ${D}${datadir}/smbios
    install -m 0644 -D ${WORKDIR}/smbios2 ${D}${datadir}/smbios/smbios2

    # Install default smbios service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/default-smbios.service ${D}${systemd_system_unitdir}

    # Replace service file only without entity manager
    if [ "${DISTRO}" != "olympus-entity" ];then
        install -m 0644 ${WORKDIR}/smbios-mdrv2.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/xyz.openbmc_project.cpuinfo.service ${D}${systemd_system_unitdir}
    fi
}

