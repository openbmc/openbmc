SUMMARY = "libcloudproviders is a DBus API that allows cloud storage sync clients to expose their services."
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

VALA_MESON_OPTION ?= 'vapigen'

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala

SRC_URI[archive.sha256sum] = "3e123fa2a34f6dfd99897ebe5fec3dfa35d9cf4b192c28fe7fc90c5aafafcf5b"
