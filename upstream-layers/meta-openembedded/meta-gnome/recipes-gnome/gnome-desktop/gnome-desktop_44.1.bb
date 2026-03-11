SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
"

inherit gnomebase itstool pkgconfig upstream-version-is-even gobject-introspection features_check gtk-doc

REQUIRED_DISTRO_FEATURES = "opengl"

GIR_MESON_OPTION = ""

SRC_URI += "file://gnome-desktop-thumbnail-don-t-assume-time_t-is-long.patch \
            file://0001-meson-Add-riscv32-to-seccomp-unsupported-list.patch"
SRC_URI[archive.sha256sum] = "ae7ca55dc9e08914999741523a17d29ce223915626bd2462a120bf96f47a79ab"

DEPENDS += " \
    fontconfig \
    gdk-pixbuf \
    glib-2.0 \
    gsettings-desktop-schemas \
    iso-codes \
    libseccomp \
    libxkbcommon \
    xkeyboard-config \
"

DEPENDS:remove:riscv32 = "libseccomp"

GTKDOC_MESON_OPTION = "gtk_doc"
EXTRA_OEMESON = "-Ddesktop_docs=false"

PACKAGECONFIG ??= "gtk4 legacy"
PACKAGECONFIG[gtk4] = "-Dbuild_gtk4=true,-Dbuild_gtk4=false,gtk4"
PACKAGECONFIG[legacy] = "-Dlegacy_library=true,-Dlegacy_library=false,gtk+3"

PACKAGES =+ "libgnome-desktop"
RDEPENDS:${PN} += "libgnome-desktop"
FILES:libgnome-desktop = " \
    ${libdir}/lib*${SOLIBS} \
    ${datadir}/libgnome-desktop*/pnp.ids \
    ${datadir}/gnome/*xml \
"

RRECOMMENDS:libgnome-desktop += "gsettings-desktop-schemas"
