SUMMARY = "Enables backwards compatibility with current desktop icon themes"
DESCRIPTION = "A Perl script used for maintaining backwards \
compatibility with current desktop icon themes, while migrating to the \
names specified in the Icon Naming Specification. The Icon Naming \
Utilities map the icon names used by the GNOME and KDE desktops to the \
icon names proposed in the Icon Naming Specification, and generate the \
icon files appropriate to the desktop environment you use. The Icon \
Naming Specification provides a standard list of common icon contexts \
and names that can be used to make icon themes work in a desktop \
environment or application that implements the naming specification. "
LICENSE = "GPLv2"
DEPENDS = "libxml-simple-perl-native"
PR = "r5"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://tango.freedesktop.org/releases/icon-naming-utils-${PV}.tar.gz"
SRC_URI[md5sum] = "2c5c7a418e5eb3268f65e21993277fba"
SRC_URI[sha256sum] = "044ab2199ed8c6a55ce36fd4fcd8b8021a5e21f5bab028c0a7cdcf52a5902e1c"

inherit autotools allarch perlnative

do_configure_append() {
	# Make sure we use our nativeperl wrapper.
	sed -i -e "1s:#!.*:#!/usr/bin/env nativeperl:" ${S}/icon-name-mapping.pl.in
}

FILES_${PN} += "${datadir}/dtds"

BBCLASSEXTEND = "native"
