SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "gnome-desktop"

inherit gnome pkgconfig
SRC_URI[archive.md5sum] = "ab5bf4cc94ad63639f42adcc1542b1f0"
SRC_URI[archive.sha256sum] = "3a8f196b46eb9dbd3ba2afb8fb5fef6a8825539d449a02181311242e22227bd0"

DEPENDS += "gsettings-desktop-schemas gconf libxrandr virtual/libx11 gtk+3 glib-2.0 gnome-doc-utils gnome-common startup-notification xkeyboard-config iso-codes"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--disable-desktop-docs"

PACKAGES =+ "libgnome-desktop3"
FILES_libgnome-desktop3 = "${libdir}/lib*${SOLIBS} ${datadir}/libgnome-desktop*/pnp.ids ${datadir}/gnome/*xml"

RRECOMMENDS_libgnome-desktop3 += "gsettings-desktop-schemas"

