SUMMARY = "C++ wrapper for libxml library"
DESCRIPTION = "C++ wrapper for libxml library"
HOMEPAGE = "http://libxmlplusplus.sourceforge.net"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml%2B%2B"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 "

DEPENDS = "libxml2 glibmm"

GNOMEBN = "libxml++"
inherit gnomebase ptest

S = "${WORKDIR}/libxml++-${PV}"

SRC_URI[archive.sha256sum] = "15c38307a964fa6199f4da6683a599eb7e63cc89198545b36349b87cf9aa0098"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/libxml++-${@gnome_verdir("${PV}")}/include/libxml++config.h"
