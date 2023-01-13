SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD mirrors a block device over the network to another machine.\
DRBD mirrors a block device over the network to another machine.\
Think of it as networked raid 1. It is a building block for\
setting up high availability (HA) clusters."
HOMEPAGE = "http://www.drbd.org/"
SECTION = "admin"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"

SRC_URI = "git://github.com/LINBIT/drbd-utils;name=drbd-utils;branch=master;protocol=https \
           git://github.com/LINBIT/drbd-headers;name=drbd-headers;destsuffix=git/drbd-headers;branch=master;protocol=https \
           file://0001-drbdmon-add-LDFLAGS-when-linking.patch \
           file://0001-replace-off64_t-with-off_t.patch \
           ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','file://0001-drbd-utils-support-usrmerge.patch','',d)} \
           "
SRC_URI:append:libc-musl = " file://0002-drbdadm-drop-use-of-GLOB_MAGCHAR-use-strchr-heuristi.patch "
SRCREV_drbd-utils = "409097fe02187f83790b88ac3e0d94f3c167adab"
SRCREV_drbd-headers = "9a0f151fa0085f57910a2dcbbd658d6069554f62"

SRCREV_FORMAT = "drbd-utils_drbd-headers"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/LINBIT/drbd-utils/releases"

SYSTEMD_SERVICE:${PN} = "drbd.service"
SYSTEMD_AUTO_ENABLE = "disable"

DEPENDS = "flex-native"

inherit autotools-brokensep systemd

EXTRA_OECONF = " \
                --with-initdir=/etc/init.d    \
                --without-pacemaker           \
                --without-rgmanager           \
                --without-bashcompletion      \
                --with-distro debian          \
                --with-initscripttype=both    \
                --with-systemdunitdir=${systemd_unitdir}/system \
                --without-manual \
               "

# If we have inherited reproducible_build, we want to use it.
export WANT_DRBD_REPRODUCIBLE_BUILD = "yes"

do_install:append() {
    # don't install empty /var/lock and /var/run to avoid conflict with base-files
    rm -rf ${D}${localstatedir}/lock
    rm -rf ${D}${localstatedir}/run

    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/drbd-demote-or-escalate@.service
    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/drbd-promote@.service
    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/drbd-wait-promotable@.service
    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/drbd.service
    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/drbd@.service
    sed -i -e 's#@nonarch_libdir@#${nonarch_libdir}#g' ${D}${systemd_unitdir}/system/ocf.ra@.service
}

RDEPENDS:${PN} += "bash perl-module-getopt-long perl-module-exporter perl-module-constant perl-module-overloading perl-module-exporter-heavy"

# The drbd items are explicitly put under /lib when installed.
#
FILES:${PN} += "/run"
FILES:${PN} += "${nonarch_base_libdir}/drbd \
                ${nonarch_libdir}/drbd \
                ${nonarch_libdir}/tmpfiles.d \
                ${nonarch_libdir}/drbdscripts/* \
                ${systemd_unitdir}/system/* \
"
FILES:${PN}-dbg += "${nonarch_base_libdir}/drbd/.debug"

CLEANBROKEN = "1"
