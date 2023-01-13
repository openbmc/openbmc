SUMMARY = "libdecor - A client-side decorations library for Wayland clients"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "git://gitlab.gnome.org/jadahl/libdecor.git;protocol=https;branch=master"

DEPENDS = " \
	cairo \
	pango \
	wayland \
	wayland-native \
	wayland-protocols \
"

S = "${WORKDIR}/git"
SRCREV = "e87dcfdaf83f332fa83b43c804fcf93c151ff0f5"

PACKAGECONFIG ?= "dbus ${@bb.utils.filter('DISTRO_FEATURES', 'gtk3 opengl', d)}"

PACKAGECONFIG[dbus] = "-Ddbus=enabled,-Ddbus=disabled,dbus"
PACKAGECONFIG[demo] = "-Ddemo=true,-Ddemo=false,virtual/libegl libxkbcommon"
PACKAGECONFIG[gtk3] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[opengl] = ",,virtual/libgl"

inherit meson pkgconfig

EXTRA_OEMESON += "--buildtype release"

BBCLASSEXTEND = "native nativesdk"
