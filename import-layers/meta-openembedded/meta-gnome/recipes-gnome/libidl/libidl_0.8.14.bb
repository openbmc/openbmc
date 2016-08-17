require libidl.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "glib-2.0 flex-native libidl-native"

PR = "r1"

BINCONFIG_GLOB = "*-config-2"
inherit autotools pkgconfig binconfig

SRC_URI[md5sum] = "bb8e10a218fac793a52d404d14adedcb"
SRC_URI[sha256sum] = "c5d24d8c096546353fbc7cedf208392d5a02afe9d56ebcc1cccb258d7c4d2220"
