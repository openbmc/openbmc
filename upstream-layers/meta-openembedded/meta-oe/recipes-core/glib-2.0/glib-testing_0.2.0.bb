SUMMARY = "libglib-testing provides test harnesses and mock classes to complement GLib classes"
HOMEPAGE = "https://gitlab.gnome.org/pwithnall/libglib-testing"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://gitlab.gnome.org/pwithnall/libglib-testing.git;protocol=https;branch=main"

PV = "0.2.0"
SRCREV = "605ca3b43c7e57b443ce43325c2f2ce1fd29ed82"

inherit  meson pkgconfig

DEPENDS = "glib-2.0 gtk-doc-native libxslt-native docbook-xsl-stylesheets-native python3-pygments-native"
