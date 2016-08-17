SUMMARY = "Compatibility library for accessing secrets"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0914b9d3ebaba41ef2e3e0ae16f296cf \
                    file://library/gnome-keyring.h;endline=25;md5=68ea64f81c160d670c37da5f137be4fb \
                    file://library/gnome-keyring.c;endline=26;md5=8d7a4fb674aaa012ea5a98e7c368b4a5 \
                    file://egg/egg-dh.h;endline=22;md5=1626c16af2a8da1f88324cf3ced33f08"

SECTION = "x11/gnome/libs"
PR = "r3"

inherit gnomebase gtk-doc

DEPENDS = "dbus libgcrypt glib-2.0 intltool-native"

SRC_URI[archive.md5sum] = "c42b2ca66204835d901d3dbfc1fa5ae6"
SRC_URI[archive.sha256sum] = "56388c0d81ddfdb57d30e4963c83ecc1c18498aab99395420e0fff69929a0f0c"
GNOME_COMPRESS_TYPE="bz2"
