SUMMARY = "Mickey's DBus Introspection and Interaction Utility V2"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07"

DEPENDS = "readline"

PV = "2.3.3+git"

SRC_URI = "git://github.com/freesmartphone/mdbus.git;protocol=https;branch=master \
           file://0001-Fix-arguments-in-GLib.DBusSignalCallback-for-Vala-0..patch \
           "
SRCREV = "28202692d0b441000f4ddb8f347f72d1355021aa"

S = "${WORKDIR}/git"

inherit autotools pkgconfig vala

EXTRA_OECONF += "--enable-vala"
