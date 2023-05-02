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
          libarchive \
          libdazzle \
          libhandy \
          glib-2.0-native \
          coreutils-native \
          "

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gsettings features_check gettext mime-xdg
REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@oe.utils.trim_version("${PV}", 1)}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0002-help-meson.build-disable-the-use-of-yelp.patch \
           file://migrator.patch \
           file://distributor.patch \
           file://CVE-2023-26081.patch \
           "
SRC_URI[archive.sha256sum] = "370938ad2920eeb28bc2435944776b7ba55a0e2ede65836f79818cfb7e8f0860"

PACKAGECONFIG_SOUP ?= "soup2"
PACKAGECONFIG ??= "${PACKAGECONFIG_SOUP}"

# Developer mode enables debugging
PACKAGECONFIG[developer-mode] = "-Ddeveloper_mode=true,-Ddeveloper_mode=false"
PACKAGECONFIG[soup2] = "-Dsoup2=enabled,-Dsoup2=disabled,libsoup-2.4,,,soup3"
PACKAGECONFIG[soup3] = ",,libsoup,,,soup2"
PACKAGECONFIG[libportal] = "-Dlibportal=enabled,-Dlibportal=disabled,libportal"

FILES:${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers ${datadir}/metainfo"
RDEPENDS:${PN} = "iso-codes adwaita-icon-theme gsettings-desktop-schemas"
