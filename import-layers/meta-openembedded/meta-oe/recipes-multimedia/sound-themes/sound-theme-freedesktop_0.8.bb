SUMMARY = "Freedesktop sound theme"
HOMEPAGE = "http://freedesktop.org/wiki/Specifications/sound-theme-spec"
LICENSE = "GPLv2+ & CC-BY-3.0 & CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://CREDITS;md5=3213e601ce34bb42ddc3498903ac4e69"

# glib-2.0 for glib-gettext.m4 which provides AM_GLIB_GNU_GETTEXT
# intltool for intltool.m4 which provides IT_PROG_INTLTOOL
DEPENDS = "glib-2.0 intltool-native"

inherit autotools gettext

DEPENDS += "glib-2.0-native intltool-native"

SRC_URI = "http://people.freedesktop.org/~mccann/dist/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "d7387912cfd275282d1ec94483cb2f62"
SRC_URI[sha256sum] = "cb518b20eef05ec2e82dda1fa89a292c1760dc023aba91b8aa69bafac85e8a14"
