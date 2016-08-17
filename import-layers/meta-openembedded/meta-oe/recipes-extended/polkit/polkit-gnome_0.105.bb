SUMMARY = "GNOME Authentication Agent for PolicyKit"
DESCRIPTION = "PolicyKit-gnome provides an Authentication Agent for PolicyKit that integrates well with the GNOME desktop environment"
HOMEPAGE = "http://www.packagekit.org/"
BUGTRACKER = "http://bugzilla.gnome.org/"
DEPENDS = "polkit dbus-glib gconf gtk+ intltool-native gnome-common"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=74579fab173e4c5e12aac0cd83ee98ec \
                    file://src/main.c;beginline=1;endline=20;md5=aba145d1802f2329ba561e3e48ecb795"

SRC_URI = "https://download.gnome.org/sources/polkit-gnome/${PV}/polkit-gnome-${PV}.tar.xz \
           file://gtk-doc-check.patch \
"
SRC_URI[md5sum] = "50ecad37c8342fb4a52f590db7530621"
SRC_URI[sha256sum] = "1784494963b8bf9a00eedc6cd3a2868fb123b8a5e516e66c5eda48df17ab9369"

EXTRA_OECONF = "\
    --disable-static \
"

DEPENDS += "gtk+3"

inherit autotools gtk-doc pkgconfig

FILES_${PN} += " ${datadir}/dbus-1 \
                 ${datadir}/PolicyKit \
"
