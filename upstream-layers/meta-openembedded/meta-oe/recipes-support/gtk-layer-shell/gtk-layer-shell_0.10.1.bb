SUMMARY = "A library to write GTK applications that use Layer Shell"
HOMEPAGE = "https://github.com/wmww/gtk-layer-shell"
BUGTRACKER = "https://github.com/www/gtk-layer-shell/issues"
SECTION = "graphics"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE_MIT.txt;md5=ffeb3391e5dace600b84c757688b9f1b"

REQUIRED_DISTRO_FEATURES = "wayland gobject-introspection-data"

DEPENDS += " \
	gtk+3 \
	wayland \
	wayland-native \
	wayland-protocols \
"

SRC_URI = " \
	git://github.com/wmww/gtk-layer-shell.git;protocol=https;branch=master;tag=v${PV} \
"
SRCREV = "fd88ba666c18ff65ea786bf7b2e270d840030817"

inherit meson pkgconfig features_check gobject-introspection vala

EXTRA_OEMESON += "--buildtype release"
