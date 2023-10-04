SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
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

RDEPENDS:${PN} = "acl"

SRC_URI = " \
	git://github.com/storaged-project/udisks.git;branch=2.10.x-branch;protocol=https \
	file://0001-Makefile.am-Dont-include-buildpath.patch \
"
SRCREV = "18c9faf089e306ad6f3f51f5cb887a6b9aa08350"
S = "${WORKDIR}/git"

CVE_PRODUCT = "udisks"

inherit autotools-brokensep systemd gtk-doc gobject-introspection gettext features_check

REQUIRED_DISTRO_FEATURES = "polkit"

EXTRA_OECONF = "--disable-man --disable-gtk-doc"

do_configure:prepend() {
    # | configure.ac:656: error: required file 'build-aux/config.rpath' not found
    mkdir -p ${S}/build-aux
    touch ${S}/build-aux/config.rpath
}

PACKAGECONFIG ?= ""

PACKAGECONFIG[lvm2] = "--enable-lvm2,--disable-lvm2,lvm2"
PACKAGECONFIG[btrfs] = "--enable-btrfs,--disable-btrfs,,btrfs-tools"
PACKAGECONFIG[lsm] = "--enable-lsm,--disable-lsm,libstoragemgmt"

FILES:${PN} += " \
    ${datadir}/dbus-1/ \
    ${datadir}/polkit-1 \
    ${datadir}/bash-completion \
    ${datadir}/zsh \
    ${libdir}/polkit-1/extensions/*.so \
    ${nonarch_base_libdir}/udev/* \
    ${exec_prefix}${nonarch_base_libdir}/udisks2/* \
    ${systemd_system_unitdir} \
"

PACKAGES =+ "${PN}-libs"
FILES:${PN}-libs = "${libdir}/lib*${SOLIBS}"
FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"
