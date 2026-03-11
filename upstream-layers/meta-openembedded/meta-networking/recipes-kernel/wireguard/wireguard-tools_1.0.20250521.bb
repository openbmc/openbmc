require wireguard.inc

SRCREV = "e2ecaaa739144997ccff89d6ad6ec81698ea6ced"
SRC_URI = "git://github.com/WireGuard/wireguard-tools.git;branch=master;protocol=https"

inherit bash-completion systemd pkgconfig

DEPENDS += "libmnl"

do_install () {
    oe_runmake DESTDIR="${D}" PREFIX="${prefix}" SYSCONFDIR="${sysconfdir}" \
        SYSTEMDUNITDIR="${systemd_system_unitdir}" \
        WITH_SYSTEMDUNITS=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', '', d)} \
        ${PACKAGECONFIG_CONFARGS} \
        install
}

PACKAGECONFIG ??= "bash-completion wg-quick"

PACKAGECONFIG[bash-completion] = "WITH_BASHCOMPLETION=yes,WITH_BASHCOMPLETION=no,,bash,,"
PACKAGECONFIG[wg-quick]        = "WITH_WGQUICK=yes,WITH_WGQUICK=no,,bash,,"

FILES:${PN} = " \
    ${bindir}/wg \
    ${sysconfdir} \
    ${bindir}/wg-quick \
    ${systemd_system_unitdir} \
"

RRECOMMENDS:${PN} = " \
    kernel-module-wireguard \
    "
