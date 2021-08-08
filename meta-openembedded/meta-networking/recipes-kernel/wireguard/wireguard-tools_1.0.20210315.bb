require wireguard.inc

SRCREV = "622408872fd6f3a58e98e88d39d30e98968314fa"
SRC_URI = "git://git.zx2c4.com/wireguard-tools"

inherit bash-completion systemd pkgconfig

DEPENDS += "libmnl"

do_install () {
    oe_runmake DESTDIR="${D}" PREFIX="${prefix}" SYSCONFDIR="${sysconfdir}" \
        SYSTEMDUNITDIR="${systemd_system_unitdir}" \
        WITH_SYSTEMDUNITS=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', '', d)} \
        WITH_BASHCOMPLETION=yes \
        WITH_WGQUICK=yes \
        install
}

FILES:${PN} = " \
    ${sysconfdir} \
    ${systemd_system_unitdir} \
    ${bindir} \
"

RDEPENDS:${PN} = "bash"
RRECOMMENDS:${PN} = "kernel-module-wireguard"
