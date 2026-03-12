DESCRIPTION = "keybinder is a library for registering global key bindings, for gtk-based applications."
HOMEPAGE = "https://github.com/engla/keybinder"
LICENSE = "MIT"
SECTION = "devel/lib"
DEPENDS = "gtk+3 gobject-introspection-native \
           gtk+ \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=1f18f9c6d8b4cfcc7d7804a243a4c0b4"


SRCREV = "04ae06724d914c7d4fec6a2723edf9c6320ec502"
PV = "3.0+git"
SRC_URI = "git://github.com/engla/keybinder.git;branch=master;protocol=https;tag=keybinder-3.0-v0.3.2 \
"

RDEPENDS:${PN} = "gtk+"

inherit features_check autotools-brokensep gtk-doc gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"
do_configure:prepend() {
	touch ${S}/ChangeLog
}

# gtk-doc generation doesn't work, so disable it:
# ../keybinder-docs.sgml:26: element include: XInclude error : could not load ../xml/tree_index.sgml, and no fallback was found
GTKDOC_ENABLED = "False"
