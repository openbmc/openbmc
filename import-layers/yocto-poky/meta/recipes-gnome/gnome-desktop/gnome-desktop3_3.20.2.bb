SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "gnome-desktop"

inherit gnome pkgconfig upstream-version-is-even gobject-introspection
SRC_URI[archive.md5sum] = "8354ed38624f5eb6b5d34267b658a9c9"
SRC_URI[archive.sha256sum] = "492c2da7aa8c3a8b65796e8171fc8f0dfb5d322dd2799c0d76392e1fb061e2b2"

DEPENDS += "intltool-native gsettings-desktop-schemas gconf libxrandr virtual/libx11 gtk+3 glib-2.0 startup-notification xkeyboard-config iso-codes"

inherit distro_features_check gtk-doc
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--disable-desktop-docs"

PACKAGES =+ "libgnome-desktop3"
FILES_libgnome-desktop3 = "${libdir}/lib*${SOLIBS} ${datadir}/libgnome-desktop*/pnp.ids ${datadir}/gnome/*xml"

RRECOMMENDS_libgnome-desktop3 += "gsettings-desktop-schemas"
