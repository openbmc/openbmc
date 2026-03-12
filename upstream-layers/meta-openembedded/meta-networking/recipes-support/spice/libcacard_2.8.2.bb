SUMMARY = "This library provides emulation of smart cards to a virtual card reader running in a guest virtual machine."
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=79ffa0ec772fa86740948cb7327a0cc7"

DEPENDS = "glib-2.0 pcsc-lite nss"

SRCREV = "df2d3c05a250741632184c2cc66df247039a70d7"

SRC_URI = "git://gitlab.freedesktop.org/spice/libcacard.git;protocol=https;branch=master;tag=v${PV}"


inherit meson pkgconfig

do_configure:prepend() {
	echo ${PV} > ${S}/.tarball-version
}
