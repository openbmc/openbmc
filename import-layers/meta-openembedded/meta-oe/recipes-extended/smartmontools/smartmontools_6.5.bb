SUMMARY = "Control and monitor storage systems using S.M.A.R.T"
DESCRIPTION = \
"The smartmontools package contains two utility programs (smartctl \
and smartd) to control and monitor storage systems using the Self-\
Monitoring, Analysis and Reporting Technology System (SMART) built \
into most modern ATA and SCSI hard disks. In many cases, these \
utilities will provide advanced warning of disk degradation and failure."

HOMEPAGE = "http://smartmontools.sourceforge.net/"
SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SOURCEFORGE_MIRROR}/smartmontools/smartmontools-${PV}.tar.gz \
    file://initd.smartd \
    file://smartmontools.default \
    file://smartd.service \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'libcap-ng selinux', d)}"
PACKAGECONFIG[libcap-ng] = "--with-libcap-ng=yes,--with-libcap-ng=no,libcap-ng"
PACKAGECONFIG[selinux] = "--with-selinux=yes,--with-selinux=no,libselinux"

SRC_URI[md5sum] = "093aeec3f8f39fa9a37593c4012d3156"
SRC_URI[sha256sum] = "89e8bb080130bc6ce148573ba5bb91bfe30236b64b1b5bbca26515d4b5c945bc"

inherit autotools update-rc.d systemd

SYSTEMD_SERVICE_${PN} = "smartd.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install_append () {
    #install the init.d/smartd
    install -d ${D}${sysconfdir}/init.d
    install -p -m 0755 ${WORKDIR}/initd.smartd ${D}${sysconfdir}/init.d/smartd
    install -d ${D}${sysconfdir}/default
    install -p -m 0644 ${WORKDIR}/smartmontools.default ${D}${sysconfdir}/default/smartmontools

    #install systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/smartd.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,@SBINDIR@,${sbindir},g' \
        ${D}${systemd_unitdir}/system/smartd.service
}

INITSCRIPT_NAME = "smartd"
INITSCRIPT_PARAMS = "start 60 2 3 4 5 . stop 60 0 1 6 ."

RDEPENDS_${PN} += "mailx"
