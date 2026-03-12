# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "The simple GObject game controller library"
HOMEPAGE = "https://gnome.pages.gitlab.gnome.org/libmanette/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libevdev libgudev hidapi"

SRC_URI = "https://download.gnome.org/sources/libmanette/0.2/libmanette-${PV}.tar.xz \
           file://run-ptest"
SRC_URI[sha256sum] = "287cc2fde0de092919366af757d85e7b3a0248eb1b395344726e97955a77d8ae"

inherit meson pkgconfig gobject-introspection ptest vala

FILES:${PN}-dev =+ "${libdir}/girepository-1.0"
FILES:${PN}-ptest += "${libexecdir}/installed-tests"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)}"
PACKAGECONFIG[test] = "-Dbuild-tests=true -Dinstall-tests=true,-Dbuild-tests=false -Dinstall-tests=false"
