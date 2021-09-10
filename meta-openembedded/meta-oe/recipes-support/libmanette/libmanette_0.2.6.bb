# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "The simple GObject game controller library"
HOMEPAGE = "https://gnome.pages.gitlab.gnome.org/libmanette/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libevdev libgudev"

SRC_URI = "https://download.gnome.org/sources/libmanette/0.2/libmanette-${PV}.tar.xz"
SRC_URI[sha256sum] = "63653259a821ec7d90d681e52e757e2219d462828c9d74b056a5f53267636bac"

inherit meson pkgconfig gobject-introspection ptest vala

FILES:${PN}-ptest =+ "${bindir}/manette-test"
FILES:${PN}-dev =+ "${libdir}/girepository-1.0"
