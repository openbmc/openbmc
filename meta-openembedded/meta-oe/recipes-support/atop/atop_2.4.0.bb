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

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "ncurses zlib"

SRC_URI = "http://www.atoptool.nl/download/${BP}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://volatiles.atop.conf', 'file://volatiles.99_atop', d)} \
           file://fix-permissions.patch \
           file://sysvinit-implement-status.patch \
           file://0001-atop.daily-atop.init-atop-pm.sh-Avoid-using-bash.patch \
           "
SRC_URI[md5sum] = "1077da884ed94f2bc3c81ac3ab970436"
SRC_URI[sha256sum] = "be1c010a77086b7d98376fce96514afcd73c3f20a8d1fe01520899ff69a73d69"

CVE_STATUS[CVE-2011-3618] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

do_compile() {
    oe_runmake all
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        make DESTDIR=${D} VERS=${PV} SYSDPATH=${systemd_system_unitdir} \
            PMPATHD=${systemd_unitdir}/system-sleep systemdinstall
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 644 ${UNPACKDIR}/volatiles.atop.conf ${D}${sysconfdir}/tmpfiles.d/atop.conf
        rm -f ${D}${systemd_system_unitdir}/atopacct.service
    else
        make DESTDIR=${D} VERS=${PV} sysvinstall
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 644 ${UNPACKDIR}/volatiles.99_atop ${D}${sysconfdir}/default/volatiles/99_atop
        rm -f ${D}${sysconfdir}/init.d/atopacct
    fi

    # /var/log/atop will be created in runtime
    rm -rf ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    # remove atopacct related files
    rm -rf ${D}${sbindir} ${D}${mandir}/man8
}

inherit systemd

SYSTEMD_SERVICE:${PN} = "atop.service atopgpu.service"
SYSTEMD_AUTO_ENABLE = "disable"

FILES:${PN} += "${systemd_unitdir}/system-sleep"

RDEPENDS:${PN} = "procps"
