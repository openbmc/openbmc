DESCRIPTION = "keybinder is a library for registering global key bindings, for gtk-based applications."
HOMEPAGE = "https://github.com/engla/keybinder"
LICENSE = "X11"
SECTION = "devel/lib"
DEPENDS = "gtk+3 gobject-introspection-native \
           gtk+ \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=1f18f9c6d8b4cfcc7d7804a243a4c0b4"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "736ccef40d39603b8111c8a3a0bca0319bbafdc0"
PV = "3.0+git${SRCPV}"
SRC_URI = "git://github.com/engla/keybinder.git;branch=keybinder-3.0 \
"

RDEPENDS_${PN} = "gtk+"

inherit autotools gtk-doc gobject-introspection
do_configure_prepend() {
	touch ${S}/ChangeLog
}

SRC_DISTRIBUTE_LICENSES += "X11"
