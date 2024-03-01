# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "The simple GObject game controller library"
HOMEPAGE = "https://gnome.pages.gitlab.gnome.org/libmanette/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libevdev libgudev"

SRC_URI = "https://download.gnome.org/sources/libmanette/0.2/libmanette-${PV}.tar.xz"
SRC_URI[sha256sum] = "cddd5c02a131072c19c6cde6f2cb2cd57eae7dacb50d14c337efd980baa51a51"

inherit meson pkgconfig gobject-introspection ptest vala

FILES:${PN}-ptest =+ "${bindir}/manette-test"
FILES:${PN}-dev =+ "${libdir}/girepository-1.0"
