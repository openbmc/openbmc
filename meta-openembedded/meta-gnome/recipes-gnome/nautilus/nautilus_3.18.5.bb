SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=36cf660aea2b8beffba7945f44a7e748 \
                    file://COPYING.EXTENSIONS;md5=7579d6678402a1868631bf82c93ff0d4 \
                    file://COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a"

inherit distro_features_check autotools pkgconfig gobject-introspection

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/nautilus/${MAJ_VER}/nautilus-${PV}.tar.xz \
           file://0001-nautilus-drop-gnome-desktop-thumbnail-API-with-new-g.patch \
           "

SRC_URI[md5sum] = "0f578bda5655c0ce204befafca5803d7"
SRC_URI[sha256sum] = "60a927c0522b4cced9d8f62baed2ee5e2fd4305be4523eb5bc44805971a6cc15"

DEPENDS = "gtk+3 gnome-desktop3 gsettings-desktop-schemas glib-2.0-native intltool-native pango"

REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = " \
    --disable-gtk-doc \
    --disable-update-mimedb \
    --disable-nst-extension \
    --enable-tracker=no \
    --disable-schemas-compile \
    --enable-xmp=no \
    --enable-libexif=no \
"

FILES_${PN} += "${datadir}/*"
