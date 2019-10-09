SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "gnome-desktop"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig upstream-version-is-even gobject-introspection distro_features_check

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

SRC_URI[archive.md5sum] = "3149bbd12eaf6f4420b1a999131ec470"
SRC_URI[archive.sha256sum] = "8d331ee655c1d56b2b97562a07c7a7598ff6706a11ff1cdce97423ebc6b62426"

SRC_URI += " \
    file://gnome-desktop-thumbnail-don-t-assume-time_t-is-long.patch \
    file://0001-meson.build-Disable-libseccomp-for-all-archs.patch \
"

DEPENDS += "itstool-native gsettings-desktop-schemas virtual/libx11 gtk+3 startup-notification xkeyboard-config iso-codes udev"

inherit distro_features_check gtk-doc
REQUIRED_DISTRO_FEATURES += "x11"

GTKDOC_MESON_OPTION = "gtk_doc"
EXTRA_OEMESON = "-Ddesktop_docs=false"

PACKAGES =+ "libgnome-desktop3"
FILES_libgnome-desktop3 = "${libdir}/lib*${SOLIBS} ${datadir}/libgnome-desktop*/pnp.ids ${datadir}/gnome/*xml"

RRECOMMENDS_libgnome-desktop3 += "gsettings-desktop-schemas"
