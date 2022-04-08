SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool pkgconfig upstream-version-is-even gobject-introspection features_check gtk-doc

REQUIRED_DISTRO_FEATURES = "x11"
# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"

GIR_MESON_OPTION = ""

SRC_URI[archive.sha256sum] = "f3caa293a5e86f6ccad18f817273db1c67061e680d79d839aa8a7528e5bb26d6"
SRC_URI += " \
    file://gnome-desktop-thumbnail-don-t-assume-time_t-is-long.patch \
    file://0001-meson.build-Disable-libseccomp-for-all-archs.patch \
"

DEPENDS += " \
    gsettings-desktop-schemas \
    virtual/libx11 \
    gtk+3 \
    gtk4 \
    startup-notification \
    xkeyboard-config \
    iso-codes \
    udev \
"

GTKDOC_MESON_OPTION = "gtk_doc"
EXTRA_OEMESON = "-Ddesktop_docs=false"

PACKAGES =+ "libgnome-desktop"
RDEPENDS:${PN} += "libgnome-desktop"
FILES:libgnome-desktop = " \
    ${libdir}/lib*${SOLIBS} \
    ${datadir}/libgnome-desktop*/pnp.ids \
    ${datadir}/gnome/*xml \
"

RRECOMMENDS:libgnome-desktop += "gsettings-desktop-schemas"
