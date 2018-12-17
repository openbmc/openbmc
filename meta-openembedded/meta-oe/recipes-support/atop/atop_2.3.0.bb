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

SRC_URI = "http://www.atoptool.nl/download/${BP}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://volatiles.atop.conf', 'file://volatiles.99_atop', d)} \
           file://remove-bashisms.patch \
           file://fix-permissions.patch \
           file://sysvinit-implement-status.patch \
           file://0001-add-sys-sysmacros.h-for-major-minor-macros.patch \
           "
SRC_URI[md5sum] = "48e1dbef8c7d826e68829a8d5fc920fc"
SRC_URI[sha256sum] = "73e4725de0bafac8c63b032e8479e2305e3962afbe977ec1abd45f9e104eb264"

do_compile() {
    oe_runmake all
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        make DESTDIR=${D} VERS=${PV} SYSDPATH=${systemd_system_unitdir} \
            PMPATHD=${systemd_unitdir}/system-sleep systemdinstall
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 644 ${WORKDIR}/volatiles.atop.conf ${D}${sysconfdir}/tmpfiles.d/atop.conf
        rm -f ${D}${systemd_system_unitdir}/atopacct.service
    else
        make DESTDIR=${D} VERS=${PV} sysvinstall
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
