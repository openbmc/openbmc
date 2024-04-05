SUMMARY = "API for managing information about operating systems, hypervisors and the (virtual) hardware devices."
HOMEPAGE = "https://libosinfo.org"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b0e9926530c269f5ae95560370195af"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = " \
	git://gitlab.com/libosinfo/libosinfo.git;branch=main;protocol=https \
	file://0001-meson.build-allow-crosscompiling-gir.patch \
	file://0001-osinfo-Make-xmlError-struct-constant-in-propagate_li.patch \
"

SRCREV = "ca9dd5b810dc04ea38048ae9be491654c8596ef9"

S = "${WORKDIR}/git"

inherit meson pkgconfig gtk-doc gobject-introspection vala

DEPENDS = "glib-2.0 libsoup libxslt"

VALA_MESON_OPTION = 'enable-vala'
VALA_MESON_ENABLE_FLAG = 'enabled'
VALA_MESON_DISABLE_FLAG = 'disabled'
GIR_MESON_OPTION = "enable-introspection"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "enable-gtk-doc"

EXTRA_OEMESON += " \
	-Dwith-pci-ids-path=${datadir}/hwdata/pci.ids \
	-Dwith-usb-ids-path=${datadir}/hwdata/usb.ids \
"

RDEPENDS:${PN} = "hwdata"
