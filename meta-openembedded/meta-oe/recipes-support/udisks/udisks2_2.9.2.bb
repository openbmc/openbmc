SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd79f6dbbffdbc8e86b086a8f0c0ef43"

DEPENDS = " \
    glib-2.0-native \
    libxslt-native \
    acl \
    libatasmart \
    polkit \
    libgudev \
    glib-2.0 \
    dbus-glib \
    libblockdev \
"
DEPENDS += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

RDEPENDS_${PN} = "acl"

SRC_URI = "git://github.com/storaged-project/udisks.git;branch=master"
SRCREV = "da6d9480fefeb0ffdf8a84626b5096827d8d7030"
S = "${WORKDIR}/git"

CVE_PRODUCT = "udisks"

inherit autotools-brokensep systemd gtk-doc gobject-introspection gettext features_check

REQUIRED_DISTRO_FEATURES = "polkit"

EXTRA_OECONF = "--disable-man --disable-gtk-doc"

do_configure_prepend() {
    # | configure.ac:656: error: required file 'build-aux/config.rpath' not found
    mkdir -p ${S}/build-aux
    touch ${S}/build-aux/config.rpath
}

FILES_${PN} += " \
    ${datadir}/dbus-1/ \
    ${datadir}/polkit-1 \
    ${datadir}/bash-completion \
    ${libdir}/polkit-1/extensions/*.so \
    ${nonarch_base_libdir}/udev/* \
    ${exec_prefix}${nonarch_base_libdir}/udisks2/* \
    ${systemd_system_unitdir} \
"

PACKAGES =+ "${PN}-libs"
FILES_${PN}-libs = "${libdir}/lib*${SOLIBS}"
FILES_${PN} += "${nonarch_libdir}/tmpfiles.d"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"
