SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD mirrors a block device over the network to another machine.\
DRBD mirrors a block device over the network to another machine.\
Think of it as networked raid 1. It is a building block for\
setting up high availability (HA) clusters."
HOMEPAGE = "http://www.drbd.org/"
SECTION = "admin"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"

SRC_URI = "git://github.com/LINBIT/drbd-utils;name=drbd-utils;branch=${PV} \
           git://github.com/LINBIT/drbd-headers;name=drbd-headers;destsuffix=git/drbd-headers \
           file://0001-v84-Make-setup_options-definitions-as-extern.patch \
           ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','file://0001-drbd-utils-support-usrmerge.patch','',d)} \
          "
SRCREV_drbd-utils = "b24b0f7e42d500d3538d7eeffa017ec78d08f918"
SRCREV_drbd-headers = "c757cf357edef67751b8f45a6ea894d287180087"

SRCREV_FORMAT = "drbd-utils_drbd-headers"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/LINBIT/drbd-utils/releases"

SYSTEMD_SERVICE_${PN} = "drbd.service"
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

do_install_append() {
    # don't install empty /var/lock and /var/run to avoid conflict with base-files
    rm -rf ${D}${localstatedir}/lock
    rm -rf ${D}${localstatedir}/run
}

RDEPENDS_${PN} += "bash perl-module-getopt-long perl-module-exporter perl-module-constant perl-module-overloading perl-module-exporter-heavy"

# The drbd items are explicitly put under /lib when installed.
#
FILES_${PN} += "/run"
FILES_${PN} += "${nonarch_base_libdir}/drbd \
                ${nonarch_libdir}/drbd \
                ${nonarch_libdir}/tmpfiles.d"
FILES_${PN}-dbg += "${nonarch_base_libdir}/drbd/.debug"

CLEANBROKEN = "1"
