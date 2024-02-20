include ${BPN}.inc

SRCREV = "82ad07d997b0b2ee70e1b2c7e82fcc6d0ccf23ea"
PV = "1.0+git"

SRC_URI += "file://0001-Check-and-use-strlcpy-from-libc-before-defining-own.patch \
           file://0002-lib-netdev-Adjust-header-include-sequence.patch \
           file://0001-generate-not-static-get_dh-functions.patch \
           file://0001-socket-util-Include-sys-stat.h-for-fchmod.patch \
           file://0001-Makefile.am-Specify-export-dynamic-directly-to-linke.patch \
           "
