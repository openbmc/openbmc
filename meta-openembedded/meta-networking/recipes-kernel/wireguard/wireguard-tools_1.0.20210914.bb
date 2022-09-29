require wireguard.inc

SRCREV = "3ba6527130c502144e7388b900138bca6260f4e8"
SRC_URI = "git://git.zx2c4.com/wireguard-tools;branch=master"

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

PACKAGES += "${PN}-wg-quick"

FILES:${PN} = " \
    ${bindir}/wg \
    ${sysconfdir} \
"
FILES:${PN}-wg-quick = " \
    ${bindir}/wg-quick \
    ${systemd_system_unitdir} \
"

RDEPENDS:${PN}-wg-quick = "${PN} bash"
RRECOMMENDS:${PN} = " \
    kernel-module-wireguard \
    ${PN}-wg-quick \
    "
