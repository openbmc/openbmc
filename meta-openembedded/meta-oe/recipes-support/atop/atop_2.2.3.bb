SUMMARY = "Monitor for system resources and process activity"
DESCRIPTION = "Atop is an ASCII full-screen performance monitor for Linux that \
is capable of reporting the activity of all processes (even if processes have \
finished during the interval), daily logging of system and process activity for \
long-term analysis, highlighting overloaded system resources by using colors, \
etc. At regular intervals, it shows system-level activity related to the CPU, \
memory, swap, disks (including LVM) and network layers, and for every process \
(and thread) it shows e.g. the CPU utilization, memory growth, disk \
utilization, priority, username, state, and exit code."
HOMEPAGE = "http://www.atoptool.nl"
SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "ncurses zlib"

ATOP_VER = "${@'-'.join(d.getVar('PV').rsplit('.', 1))}"

SRC_URI = " \
    http://www.atoptool.nl/download/${BPN}-${ATOP_VER}.tar.gz \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://volatiles.atop.conf', 'file://volatiles.99_atop', d)} \
    file://0001-include-missing-header-files.patch \
    file://remove-bashisms.patch \
    file://fix-permissions.patch \
    file://sysvinit-implement-status.patch \
"

SRC_URI[md5sum] = "034dc1544f2ec4e4d2c739d320dc326d"
SRC_URI[sha256sum] = "c785b8a2355be28b3de6b58a8ea4c4fcab8fadeaa57a99afeb03c66fac8e055d"

S = "${WORKDIR}/${BPN}-${ATOP_VER}"

do_compile() {
    oe_runmake all
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        make DESTDIR=${D} VERS=${ATOP_VER} SYSDPATH=${systemd_system_unitdir} \
            PMPATHD=${systemd_unitdir}/system-sleep systemdinstall
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 644 ${WORKDIR}/volatiles.atop.conf ${D}${sysconfdir}/tmpfiles.d/atop.conf
        rm -f ${D}${systemd_system_unitdir}/atopacct.service
    else
        make DESTDIR=${D} VERS=${ATOP_VER} sysvinstall
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 644 ${WORKDIR}/volatiles.99_atop ${D}${sysconfdir}/default/volatiles/99_atop
        rm -f ${D}${sysconfdir}/init.d/atopacct
    fi

    # remove atopacct related files
    rm -rf ${D}${sbindir} ${D}${mandir}/man8
}

inherit systemd

SYSTEMD_SERVICE_${PN} = "atop.service"
SYSTEMD_AUTO_ENABLE = "disable"

FILES_${PN} += "${systemd_unitdir}/system-sleep"

RDEPENDS_${PN} = "procps"
