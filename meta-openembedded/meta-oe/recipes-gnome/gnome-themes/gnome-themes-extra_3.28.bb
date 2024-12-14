SUMMARY = "GTK+2 standard themes"
HOMEPAGE = "http://ftp.gnome.org/pub/GNOME/sources/gnome-themes-standard/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

GNOMEBASEBUILDCLASS = "autotools"
GTKIC_VERSION = '3'
inherit gnomebase gettext gtk-icon-cache upstream-version-is-even features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "intltool-native gtk+3 librsvg"

# PV is 3.28 not 3.28.0, so the gnomebase SRC_URI isn't right.
SRC_URI = "${GNOME_MIRROR}/${BPN}/${PV}/${BPN}-${PV}.tar.xz;name=archive"
SRC_URI[archive.md5sum] = "f9f2c6c521948da427f702372e16f826"
SRC_URI[archive.sha256sum] = "7c4ba0bff001f06d8983cfc105adaac42df1d1267a2591798a780bac557a5819"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/Archive/gnome-themes-extra/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

EXTRA_OECONF = "--disable-gtk2-engine"

do_install:append() {
        # Only building Adwaita, remove highcontrast files
        rm -rf ${D}${datadir}/themes/HighContrast \
               ${D}${datadir}/icons

	# The libtool archive file is unneeded with shared libs on modern Linux
	rm -rf ${D}${libdir}/gtk-2.0/2.10.0/engines/libadwaita.la
}

# There could be gnome-theme-highcontrast as well but that requires
# gtk+3 and includes lots of icons (is also broken with B != S).
PACKAGES += "gnome-theme-adwaita \
             gnome-theme-adwaita-dark \
             "

FILES:gnome-theme-adwaita = "${datadir}/themes/Adwaita \
                              ${libdir}/gtk-2.0/2.10.0/engines/libadwaita.so"

FILES:gnome-theme-adwaita-dark = "${datadir}/themes/Adwaita-dark"
RDEPENDS:gnome-theme-adwaita-dark = "gnome-theme-adwaita"

# gnome-themes-standard is empty and doesn't exist
RDEPENDS:${PN}-dev = ""
