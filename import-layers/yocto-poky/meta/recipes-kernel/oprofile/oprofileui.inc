SUMMARY = "User Interface for the System-Wide Profiler"
DESCRIPTION = "User interface for the OProfile tool"
HOMEPAGE = "http://labs.o-hand.com/oprofileui/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

SECTION = "x11"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "glib-2.0 avahi intltool-native"

inherit autotools pkgconfig gettext

EXTRA_OECONF = "--with-avahi"

