SUMMARY = "WebKit based web browser for GNOME"
DESCRIPTION = "Epiphany is an open source web browser for the Linux desktop environment. \
It provides a simple and easy-to-use internet browsing experience."
HOMEPAGE = "https://wiki.gnome.org/Apps/Web"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/epiphany"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
          webkitgtk \
          gcr \
          gsettings-desktop-schemas \
          nettle \
          json-glib \
          libadwaita \
          libarchive \
          libdazzle \
          libhandy \
          libportal \
          libsoup \
          glib-2.0-native \
          coreutils-native \
          desktop-file-utils-native \
          "

inherit gnomebase gsettings features_check gettext mime-xdg gtk-icon-cache
REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@oe.utils.trim_version("${PV}", 1)}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0002-help-meson.build-disable-the-use-of-yelp.patch \
           file://migrator.patch \
           file://distributor.patch \
           "
SRC_URI[archive.sha256sum] = "34dafd8363a098b44d476e2e9823b4cbea6e66afad33bba10ed7a13ff1a964f1"

# Developer mode enables debugging
PACKAGECONFIG[developer-mode] = "-Ddeveloper_mode=true,-Ddeveloper_mode=false"

FILES:${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers ${datadir}/metainfo"
RDEPENDS:${PN} = "iso-codes adwaita-icon-theme gsettings-desktop-schemas"

# ANGLE requires SSE support as of webkit 2.40.x on 32 bit x86
COMPATIBLE_HOST:x86 = "${@bb.utils.contains_any('TUNE_FEATURES', 'core2 corei7', '.*', 'null', d)}"
