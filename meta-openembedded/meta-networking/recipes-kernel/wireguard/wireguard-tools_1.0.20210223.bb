require wireguard.inc

SRCREV = "6d3b876492298d0e311053567ecd7084972a8bc8"
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

FILES_${PN} = " \
    ${sysconfdir} \
    ${systemd_system_unitdir} \
    ${bindir} \
"

RDEPENDS_${PN} = "bash"
RRECOMMENDS_${PN} = "kernel-module-wireguard"
