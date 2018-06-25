SUMMARY = "GNOME library for reading .desktop files"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "gnome-desktop"

inherit gnome pkgconfig upstream-version-is-even gobject-introspection
SRC_URI[archive.md5sum] = "6cee2ecd677d87eaa0eb5ebfa7b45fb3"
SRC_URI[archive.sha256sum] = "f7561a7a313fc474b2c390cd9696df1f5c1e1556080e43f4afe042b1060e5f2a"

SRC_URI += " \
           file://gnome-desktop-thumbnail-don-t-assume-time_t-is-long.patch \
           file://0001-configure.ac-Remove-gnome-common-macro-calls.patch \
           file://0001-Disable-libseccomp-sycall-filtering-mechanism.patch \
           "

DEPENDS += "intltool-native gsettings-desktop-schemas gconf virtual/libx11 gtk+3 glib-2.0 startup-notification xkeyboard-config iso-codes udev"

inherit distro_features_check gtk-doc
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--disable-desktop-docs"

PACKAGES =+ "libgnome-desktop3"
FILES_libgnome-desktop3 = "${libdir}/lib*${SOLIBS} ${datadir}/libgnome-desktop*/pnp.ids ${datadir}/gnome/*xml"

RRECOMMENDS_libgnome-desktop3 += "gsettings-desktop-schemas"
