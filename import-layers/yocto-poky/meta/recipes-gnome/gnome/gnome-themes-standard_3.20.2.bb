SUMMARY = "GTK+2 standard themes"
HOMEPAGE = "http://ftp.gnome.org/pub/GNOME/sources/gnome-themes-standard/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools pkgconfig gettext gtk-icon-cache upstream-version-is-even distro_features_check

ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

DEPENDS += "intltool-native gtk+"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
          "

SRC_URI[md5sum] = "adc3b1d57330561fea524842d0c0b485"
SRC_URI[sha256sum] = "9d0d9c4b2c9f9008301c3c1878ebb95859a735b7fd4a6a518802b9637e4a7915"

EXTRA_OECONF = "--disable-gtk3-engine"

do_install_append() {
        # Only building Adwaita, remove highcontrast files
        rm -rf ${D}${prefix}/share/themes/HighContrast \
               ${D}${prefix}/share/icons
}

# There could be gnome-theme-highcontrast as well but that requires
# gtk+3 and includes lots of icons (is also broken with B != S).
PACKAGES += "gnome-theme-adwaita \
             gnome-theme-adwaita-dbg \
             gnome-theme-adwaita-dev"

FILES_gnome-theme-adwaita = "${prefix}/share/themes/Adwaita \
                              ${libdir}/gtk-2.0/2.10.0/engines/libadwaita.so"
FILES_gnome-theme-adwaita-dev = "${libdir}/gtk-2.0/2.10.0/engines/libadwaita.la"
FILES_gnome-theme-adwaita-dbg = "${libdir}/gtk-2.0/2.10.0/engines/.debug/libadwaita.so"
