SUMMARY = "GNOME disk utility"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9115d11797a5e6b746b4e9b90194564"

DEPENDS = "glib-2.0 gtk+ libnotify libunique udisks avahi-ui virtual/libx11 libatasmart gnome-doc-utils intltool-native libgnome-keyring"

PR = "r4"

inherit gnomebase gtk-icon-cache
SRC_URI[archive.md5sum] = "f0366c8baebca0404d190b2d78f3582d"
SRC_URI[archive.sha256sum] = "03e461b6bda7f773f8018d25fa3213d3073d4dc83a76e6b39d962652f4de6a98"
GNOME_COMPRESS_TYPE="bz2"

SRC_URI += "\
    file://disable-scrollkeeper.patch \
    file://fix-dbus-interfaces.patch \
    file://sysrooted-pkg-config.patch \
    file://0001-Add-support-for-DeviceAutomountHint.patch \
    file://0002-Require-libnotify-0.6.1.patch \
"

EXTRA_OECONF += "--disable-scrollkeeper"

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "--enable-nautilus,--disable-nautilus,nautilus"

do_configure_prepend() {
    sed -i -e "s: help : :g" ${S}/Makefile.am
}

PACKAGES =+ "${PN}-nautilus-extension ${PN}-libs"
FILES_${PN}-nautilus-extension += "${libdir}/nautilus/extensions-2.0/*.so"
FILES_${PN}-libs += "${libdir}/libgdu*.so.*"
FILES_${PN}-dev += "${libdir}/nautilus/extensions-2.0/*.la"
FILES_${PN}-staticdev += "${libdir}/nautilus/extensions-2.0/*.a"
FILES_${PN}-dbg += "${libdir}/nautilus/extensions-2.0/.debug"
