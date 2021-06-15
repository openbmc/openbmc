DESCRIPTION = "Liboop is a low-level event loop management library for POSIX-based operating systems"
HOMEPAGE = "http://www.lysator.liu.se/liboop/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=8b54f38ccbd44feb80ab90e01af8b700"

SRC_URI = "http://ftp.lysator.liu.se/pub/liboop/liboop-${PV}.tar.gz \
           file://tcl_dev.patch \
"

SRC_URI[md5sum] = "f2b3dff17355fd9a6e2229caca8993f0"
SRC_URI[sha256sum] = "56af16ad65e7397dadc8268e37ff6f67431db390c60c75e21a33e12b0e0d17e0"

PACKAGECONFIG ?= ""
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[glib] = "--with-glib,--without-glib,glib-2.0 libpcre"
PACKAGECONFIG[tcl] = "--with-tcl,--without-tcl,tcl"

EXTRA_OECONF += "--without-adns -without-libwww"

inherit autotools pkgconfig
