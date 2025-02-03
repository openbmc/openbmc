SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala gi-docgen

SRC_URI += "file://disable-lgi-check.patch"
SRC_URI[archive.sha256sum] = "376f2f73d731b54e13ddbab1d91b6382cf6a980524def44df62add15489de6dd"

PACKAGECONFIG ?= "python3 gjs lua51 ${@bb.utils.contains('DISTRO_FEATURES', 'gobject-introspection', 'vala', '', d)}"
PACKAGECONFIG:remove:riscv32 = "lua51"
PACKAGECONFIG:remove:riscv64 = "lua51"
PACKAGECONFIG:remove:powerpc64 = "lua51"
PACKAGECONFIG:remove:powerpc64le = "lua51"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject,python3-pygobject"
PACKAGECONFIG[gjs] = "-Dgjs=true,-Dgjs=false,gjs mozjs-128"
PACKAGECONFIG[lua51] = "-Dlua51=true,-Dlua51=false,luajit luajit-native,lua-lgi"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false"

FILES:${PN} += "${libdir}/libpeas-2"

