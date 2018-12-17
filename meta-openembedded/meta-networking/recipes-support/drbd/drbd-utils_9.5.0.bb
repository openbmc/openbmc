SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD mirrors a block device over the network to another machine.\
DRBD mirrors a block device over the network to another machine.\
Think of it as networked raid 1. It is a building block for\
setting up high availability (HA) clusters."
HOMEPAGE = "http://www.drbd.org/"
SECTION = "admin"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"

SRC_URI = "git://github.com/LINBIT/drbd-utils;name=drbd-utils \
           git://github.com/LINBIT/drbd-headers;name=drbd-headers;destsuffix=git/drbd-headers \
           file://0001-drbd-drbd-tools-only-rmmod-if-DRBD-is-a-module.patch \
          "
# v9.5.0
SRCREV_drbd-utils = "ee126652638328b55dc6bff47d07d6161ab768db"
SRCREV_drbd-headers = "b47cc11bcabe1a65c40ad23f71dcaf2da6419630"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/LINBIT/drbd-utils/releases"

SYSTEMD_SERVICE_${PN} = "drbd.service"
SYSTEMD_AUTO_ENABLE = "disable"

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

do_configure_prepend() {
    # move the the file under folder /lib/drbd/ to /usr/lib/drbd when usrmerge enabled
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        for m_file in `find ${S} -name 'Makefile.in'`; do
            sed -i -e "s;\$(DESTDIR)\/lib\/drbd;\$(DESTDIR)\${nonarch_libdir}\/drbd;g" $m_file
        done
        # move the the file under folder /lib/udev/ to /usr/lib/udev when usrmerge enabled
        sed -i -e "s;default_udevdir=/lib/udev;default_udevdir=\${prefix}/lib/udev;g" ${S}/configure.ac
    fi

}
do_install_append() {
    # don't install empty /var/lock to avoid conflict with base-files
    rm -rf ${D}${localstatedir}/lock
}

RDEPENDS_${PN} += "bash perl-module-getopt-long perl-module-exporter perl-module-constant perl-module-overloading perl-module-exporter-heavy"

# The drbd items are explicitly put under /lib when installed.
#
FILES_${PN} += "/run"
FILES_${PN} += "${nonarch_base_libdir}/drbd \
                ${nonarch_libdir}/drbd \
                ${nonarch_libdir}/tmpfiles.d"
FILES_${PN}-dbg += "${nonarch_base_libdir}/drbd/.debug"
