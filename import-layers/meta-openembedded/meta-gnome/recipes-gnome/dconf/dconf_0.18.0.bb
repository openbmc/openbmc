SUMMARY = "configuation database system"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SECTION = "x11/gnome"

inherit gnomebase gsettings
SRC_URI[archive.md5sum] = "69a12ed68893f2e1e81ac4e531bc1515"
SRC_URI[archive.sha256sum] = "109b1bc6078690af1ed88cb144ef5c5aee7304769d8bdc82ed48c3696f10c955"
GNOME_COMPRESS_TYPE = "xz"

DEPENDS = "dbus glib-2.0 libxml2 intltool-native"

inherit vala gtk-doc distro_features_check

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "x11", "editor", "", d)}"

#note: editor will be removed in version 0.23.1
PACKAGECONFIG[editor] = "--enable-editor,--disable-editor,gtk+3"

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains("PACKAGECONFIG", "editor", "x11", "", d)}"

EXTRA_OECONF += "--disable-man"

PACKAGES =+ "${@bb.utils.contains("DISTRO_FEATURES", "x11", "dconf-editor", "", d)}"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/gio/modules/*.so \
    ${datadir}/bash-completion \
"
FILES_dconf-editor = " \
    ${bindir}/dconf-editor \
    ${datadir}/icons \
"
FILES_${PN}-dbg += "${libdir}/gio/modules/.debug/libdconfsettings.so"
