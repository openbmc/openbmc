# Copyright (c) 2013  LG Electronics, Inc.

SUMMARY = "libnih library"
HOMEPAGE = "https://launchpad.net/libnih"
DESCRIPTION = "libnih is a small library for C application development \
    containing functions that, despite its name, are not implemented \
    elsewhere in the standard library set. \
    \
    libnih is roughly equivalent to other C libraries such as glib, \
    except that its focus is on a small size and intended for \
    applications that sit very low in the software stack, especially \
    outside of /usr. \
    \
    It expressly does not reimplement functions that already exist in \
    libraries ordinarily shipped in /lib such libc6, and does not do \
    foolish things like invent arbitrary typedefs for perfectly good C types."

SECTION = "libs"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "dbus libnih-native"
DEPENDS_class-native = "dbus-native"

SRC_URI = "https://launchpad.net/${BPN}/1.0/${PV}/+download/${BP}.tar.gz \
           file://libnih_1.0.3-4ubuntu16.patch \
           file://0001-signal.c-SIGCLD-and-SIGCHILD-are-same-on-sytem-V-sys.patch \
           "

SRC_URI[md5sum] = "db7990ce55e01daffe19006524a1ccb0"
SRC_URI[sha256sum] = "897572df7565c0a90a81532671e23c63f99b4efde2eecbbf11e7857fbc61f405"

inherit autotools
inherit gettext

# target libnih requires native nih-dbus-tool
BBCLASSEXTEND = "native"
