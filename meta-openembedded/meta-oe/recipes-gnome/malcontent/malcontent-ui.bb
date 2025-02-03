SUMMARY = "User-Interface for malcontent."
HOMEPAGE = "https://gitlab.freedesktop.org/pwithnall/malcontent"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

require malcontent.inc

DEPENDS += " \
	malcontent \
	accountsservice \
	glib-2.0 \
	glib-testing \
	dbus \
	desktop-file-utils-native \
	itstool-native \
	libpam \
	polkit \
	appstream \
	appstream-native \
	flatpak \
	libadwaita \
	gtk4 \
"

GIR_MESON_OPTION = ""

GTKIC_VERSION = "4"
inherit  meson pkgconfig gtk-icon-cache gobject-introspection gettext features_check

REQUIRED_DISTRO_FEATURES = "pam polkit gobject-introspection"

EXTRA_OEMESON = "-Dui=enabled"

do_install() {
	install -d ${D}${datadir}/gir-1.0 ${D}${libdir}/girepository-1.0 ${D}${bindir}
	install -d ${D}${datadir}/polkit-1/actions ${D}${datadir}/metainfo ${D}${datadir}/applications
	install -m 0644 ${B}/libmalcontent-ui/MalcontentUi-1.gir ${D}${datadir}/gir-1.0
	install -m 0644 ${B}/libmalcontent-ui/MalcontentUi-1.typelib ${D}${libdir}/girepository-1.0
	install -m 0644 ${B}/libmalcontent-ui/libmalcontent-ui-1.so.${PV} ${D}${libdir}
	ln -sf libmalcontent-ui-1.so.${PV} ${D}${libdir}/libmalcontent-ui-1.so
	ln -sf libmalcontent-ui-1.so.${PV} ${D}${libdir}/libmalcontent-ui-1.so.1
	install -m 0755 ${B}/malcontent-control/malcontent-control ${D}${bindir}
	install -m 0644 ${B}/malcontent-control/org.freedesktop.MalcontentControl.policy ${D}${datadir}/polkit-1/actions
	install -m 0644 ${B}/malcontent-control/org.freedesktop.MalcontentControl.metainfo.xml ${D}${datadir}/metainfo
	install -m 0644 ${B}/malcontent-control/org.freedesktop.MalcontentControl.desktop ${D}${datadir}/applications
}

FILES:${PN} += "${bindir} ${libdir} ${datadir}"

