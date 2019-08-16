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

SRC_URI[md5sum] = "b51c362b157b6407303d44f93c31ee11"
SRC_URI[sha256sum] = "61dc87c52261cfd5b94d65e8ffd923ddeb5d3944562f84942eeeb197ab8ab56a"

EXTRA_OECONF = "--disable-gtk3-engine"

do_install_append() {
        # Only building Adwaita, remove highcontrast files
        rm -rf ${D}${prefix}/share/themes/HighContrast \
               ${D}${prefix}/share/icons

	# The libtool archive file is unneeded with shared libs on modern Linux
	rm -rf ${D}${libdir}/gtk-2.0/2.10.0/engines/libadwaita.la
}

# There could be gnome-theme-highcontrast as well but that requires
# gtk+3 and includes lots of icons (is also broken with B != S).
PACKAGES += "gnome-theme-adwaita \
             gnome-theme-adwaita-dark \
             "

FILES_gnome-theme-adwaita = "${prefix}/share/themes/Adwaita \
                              ${libdir}/gtk-2.0/2.10.0/engines/libadwaita.so"

FILES_gnome-theme-adwaita-dark = "${prefix}/share/themes/Adwaita-dark"
RDEPENDS_gnome-theme-adwaita-dark = "gnome-theme-adwaita"

# gnome-themes-standard is empty and doesn't exist
RDEPENDS_${PN}-dev = ""
