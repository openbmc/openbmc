SUMMARY = "Control and monitor storage systems using S.M.A.R.T"
DESCRIPTION = "\
The smartmontools package contains two utility programs (smartctl \
and smartd) to control and monitor storage systems using the Self-\
Monitoring, Analysis and Reporting Technology System (SMART) built \
into most modern ATA and SCSI hard disks. In many cases, these \
utilities will provide advanced warning of disk degradation and failure."

HOMEPAGE = "http://smartmontools.sourceforge.net/"
SECTION = "console/utils"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SOURCEFORGE_MIRROR}/smartmontools/smartmontools-${PV}.tar.gz \
           file://initd.smartd \
           file://smartmontools.default \
           file://smartd.service \
           file://0001-configure.ac-Define-SOURCE_DATE_EPOCH-in-CPPFLAGS.patch \
           "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'libcap-ng selinux', d)}"
PACKAGECONFIG[libcap-ng] = "--with-libcap-ng=yes,--with-libcap-ng=no,libcap-ng"
PACKAGECONFIG[selinux] = "--with-selinux=yes,--with-selinux=no,libselinux"

SRC_URI[sha256sum] = "e9a61f641ff96ca95319edfb17948cd297d0cd3342736b2c49c99d4716fb993d"

inherit autotools update-rc.d systemd

SYSTEMD_SERVICE:${PN} = "smartd.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install:append () {
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

RRECOMMENDS:${PN} += "s-nail"
