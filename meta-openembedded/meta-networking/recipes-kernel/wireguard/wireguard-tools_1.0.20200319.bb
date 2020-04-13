require wireguard.inc

SRCREV = "a8063adc8ae9b4fc9848500e93f94bee8ad2e585"
SRC_URI = "git://git.zx2c4.com/wireguard-tools"

inherit bash-completion systemd pkgconfig

DEPENDS += "wireguard-module libmnl"

do_install () {
    oe_runmake DESTDIR="${D}" PREFIX="${prefix}" SYSCONFDIR="${sysconfdir}" \
        SYSTEMDUNITDIR="${systemd_unitdir}" \
        WITH_SYSTEMDUNITS=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', '', d)} \
        WITH_BASHCOMPLETION=yes \
        WITH_WGQUICK=yes \
        install
}

FILES_${PN} = " \
    ${sysconfdir} \
    ${systemd_unitdir} \
    ${bindir} \
"

RDEPENDS_${PN} = "wireguard-module bash"
