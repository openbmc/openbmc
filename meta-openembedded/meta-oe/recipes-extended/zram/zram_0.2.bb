SUMMARY = "Linux zram compressed in-memory swap"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d systemd

RDEPENDS:${PN} = "kmod \
    ${@bb.utils.contains('DISTRO_FEATURES','systemd','util-linux','util-linux-swaponoff',d)}"
RRECOMMENDS:${PN} = "kernel-module-zram"


SRC_URI = " \
           file://init \
           file://zram-swap-init \
           file://zram-swap-deinit \
           file://zram-swap.service \
           file://dev-zram0.swap \
"

do_install () {
    # Install systemd related configuration file
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/zram
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${libexecdir}
        install -m 0755 ${WORKDIR}/zram-swap-init ${D}${libexecdir}
        install -m 0755 ${WORKDIR}/zram-swap-deinit ${D}${libexecdir}
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/zram-swap.service ${D}${systemd_unitdir}/system/zram-swap.service
        sed -i -e "s,@LIBEXECDIR@,${libexecdir},g" ${D}${systemd_unitdir}/system/zram-swap.service
        install -m 0644 ${WORKDIR}/dev-zram0.swap ${D}${systemd_unitdir}/system/dev-zram0.swap
    fi
}

FILES:${PN} = " \
    ${sysconfdir} \
    ${systemd_unitdir} \
    ${libexecdir} \
"
INITSCRIPT_NAME = "zram"
INITSCRIPT_PARAMS = "start 05 2 3 4 5 . stop 22 0 1 6 ."

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "dev-zram0.swap"
