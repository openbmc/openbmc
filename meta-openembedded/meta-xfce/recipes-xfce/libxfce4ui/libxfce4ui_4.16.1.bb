SUMMARY = "Xfce4 Widget library and X Window System interaction"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native perl-native gtk+3 libxfce4util xfce4-dev-tools xfconf"

inherit xfce gtk-doc gobject-introspection features_check

# xfce4 depends on libwnck3. gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

# TODO: Check if 0001-... can go
SRC_URI += "file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch"
SRC_URI[sha256sum] = "d96946ae5af6bf078dda415419e0021909f763ee0020b42f3e26f603e51585f6"

EXTRA_OECONF += "--with-vendor-info=${DISTRO}"
EXTRA_OECONF += "--disable-vala"

PACKAGECONFIG ??= " \
       ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
"
PACKAGECONFIG[gladeui2] = "--enable-gladeui2,--disable-gladeui2,glade"
PACKAGECONFIG[x11] = "--enable-startup-notification,--disable-startup-notification,libepoxy libice libsm startup-notification"


PACKAGES += "${PN}-glade"
FILES:${PN}-glade = " \
    ${libdir}/glade \
    ${datadir}/glade \
"
