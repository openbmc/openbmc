SUMMARY = "malcontent implements support for restricting the type of content."
HOMEPAGE = "https://gitlab.freedesktop.org/pwithnall/malcontent"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

require malcontent.inc

DEPENDS = " \
	accountsservice \
	glib-2.0 \
	glib-testing \
	dbus \
	itstool-native \
	libpam \
	polkit \
"

GIR_MESON_OPTION = ""

inherit  meson pkgconfig gobject-introspection gettext features_check

REQUIRED_DISTRO_FEATURES = "pam polkit gobject-introspection"

PACKAGECONFIG ?= "ui"
PACKAGECONFIG[ui] = ",,,malcontent-ui"

EXTRA_OEMESON = "-Dui=disabled"

FILES:${PN} += " \
	${libdir}/security/pam_malcontent.so \
	${datadir}/accountsservice \
	${datadir}/help \
	${datadir}/dbus-1 \
	${datadir}/polkit-1 \
"
