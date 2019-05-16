SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd79f6dbbffdbc8e86b086a8f0c0ef43"

DEPENDS = " \
    acl \
    libatasmart \
    polkit \
    libgudev \
    dbus-glib \
    glib-2.0 \
    libblockdev \
    libxslt-native \
"
DEPENDS += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

RDEPENDS_${PN} = "acl"

SRC_URI = " \
    git://github.com/storaged-project/udisks.git;branch=master \
    file://non-gnu-libc.patch \
"
PV = "2.8.2"
SRCREV = "7a787aa3b340fc7f1ca72d748635d6c5445edc58"
S = "${WORKDIR}/git"

CVE_PRODUCT = "udisks"

inherit autotools systemd gtk-doc gobject-introspection gettext distro_features_check

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

SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"
