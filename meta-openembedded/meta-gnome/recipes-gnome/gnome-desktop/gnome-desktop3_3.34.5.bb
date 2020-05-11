SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "gnome-desktop"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool pkgconfig upstream-version-is-even gobject-introspection features_check

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

SRC_URI[archive.md5sum] = "1cbcb9d9a5159447b9a45079c6157ca5"
SRC_URI[archive.sha256sum] = "ef13203bed721d271227d9928d91937733c312ef79b6f5c00b6c02e8e0b2fa21"

SRC_URI += " \
    file://gnome-desktop-thumbnail-don-t-assume-time_t-is-long.patch \
    file://0001-meson.build-Disable-libseccomp-for-all-archs.patch \
"

DEPENDS += "gsettings-desktop-schemas virtual/libx11 gtk+3 startup-notification xkeyboard-config iso-codes udev"

inherit features_check gtk-doc
REQUIRED_DISTRO_FEATURES += "x11"

GTKDOC_MESON_OPTION = "gtk_doc"
EXTRA_OEMESON = "-Ddesktop_docs=false"

PACKAGES =+ "libgnome-desktop3"
FILES_libgnome-desktop3 = "${libdir}/lib*${SOLIBS} ${datadir}/libgnome-desktop*/pnp.ids ${datadir}/gnome/*xml"

RRECOMMENDS_libgnome-desktop3 += "gsettings-desktop-schemas"
