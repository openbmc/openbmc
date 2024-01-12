SUMMARY = "libcloudproviders is a DBus API that allows cloud storage sync clients to expose their services."
LICENSE="LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

VALA_MESON_OPTION ?= 'vapigen'

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala

SRC_URI[archive.sha256sum] = "b987456eddaf715ba9d623aa2bc501ab1b697dffeb11aaa783a637275df186da"
