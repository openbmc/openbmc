SUMMARY = "Xfce4 Widget library and X Window System interaction"
HOMEPAGE = "https://docs.xfce.org/xfce/libxfce4ui/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"
DEPENDS = "intltool-native perl-native gtk+3 libxfce4util xfce4-dev-tools xfconf"

inherit xfce gtk-doc gobject-introspection features_check

# xfce4 depends on libwnck3. gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

# TODO: Check if 0001-... can go
SRC_URI += "file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch"
SRC_URI[sha256sum] = "5d3d67b1244a10cee0e89b045766c05fe1035f7938f0410ac6a3d8222b5df907"

EXTRA_OECONF += "--with-vendor-info=${DISTRO}"
EXTRA_OECONF += "--disable-vala"

PACKAGECONFIG ??= " \
       ${@bb.utils.contains('DISTRO_FEATURES', 'opengl','x11', "", d)} \
"
PACKAGECONFIG[gladeui2] = "--enable-gladeui2,--disable-gladeui2,glade"
PACKAGECONFIG[x11] = "--enable-startup-notification,--disable-startup-notification,libepoxy libice libsm startup-notification"


PACKAGES += "${PN}-glade"
FILES:${PN}-glade = " \
    ${libdir}/glade \
    ${datadir}/glade \
"
