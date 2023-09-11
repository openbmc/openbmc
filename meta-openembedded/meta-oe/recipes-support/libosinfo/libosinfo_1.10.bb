SUMMARY = "API for managing information about operating systems, hypervisors and the (virtual) hardware devices."
HOMEPAGE = "https://libosinfo.org"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b0e9926530c269f5ae95560370195af"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = " \
	git://gitlab.com/libosinfo/libosinfo.git;branch=main;protocol=https \
	file://0001-meson.build-allow-crosscompiling-gir.patch \
"

SRCREV = "f503ff7a9e13963bcf396776bce0b209a819ba9b"

S = "${WORKDIR}/git"

inherit meson pkgconfig gtk-doc gobject-introspection vala

DEPENDS = "glib-2.0 libsoup libxslt"

GIR_MESON_OPTION = "enable-introspection"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "enable-gtk-doc"

EXTRA_OEMESON += " \
	-Dwith-pci-ids-path=${datadir}/pci.ids \
	-Dwith-usb-ids-path=${datadir}/usb.ids \
	${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Denable-vala=enabled', '-Denable-vala=disabled', d)} \
"

RDEPENDS:${PN} = "pciutils-ids usbids"
