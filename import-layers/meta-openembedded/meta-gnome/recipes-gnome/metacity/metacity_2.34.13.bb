SECTION = "x11/wm"
SUMMARY = "Metacity is the boring window manager for the adult in you"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/include/main.h;endline=24;md5=c2242df552c880280315989bab626b90"

DEPENDS = "gsettings-desktop-schemas startup-notification gtk+ gconf gdk-pixbuf-native libcanberra gnome-doc-utils libgtop intltool-native"
PR = "r1"

inherit gnomebase update-alternatives distro_features_check
# depends on startup-notification which depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

GNOME_COMPRESS_TYPE = "xz"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://remove-yelp-help-rules-var.patch \
           file://0001-ui-Define-_GNU_SOURCE.patch \
           "
SRC_URI[archive.md5sum] = "6d89b71672d4fa49fc87f83d610d0ef6"
SRC_URI[archive.sha256sum] = "8cf4dbf0da0a6f36357ce7db7f829ec685908a7792453c662fb8184572b91075"

ALTERNATIVE_${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/metacity"
ALTERNATIVE_PRIORITY = "10"

EXTRA_OECONF += "--disable-xinerama"

do_configure_prepend() {
    sed -i -e 's:$ZENITY:$NOZENITY:g' -e 's:-Werror::g' ${S}/configure.in
}

FILES_${PN} += "${datadir}/themes ${datadir}/gnome-control-center ${datadir}/gnome"
RDEPENDS_${PN} += "gsettings-desktop-schemas"

