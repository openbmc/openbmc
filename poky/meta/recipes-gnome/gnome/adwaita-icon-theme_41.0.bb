SUMMARY = "GTK+ icon theme"
DESCRIPTION = "The Adwaita icon theme is the default icon theme of the GNOME desktop \
This package package contains an icon theme for Gtk+ 3 applications."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/adwaita-icon-theme"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/adwaita-icon-theme/issues"
SECTION = "x11/gnome"

LICENSE = "LGPL-3.0-only | CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c84cac88e46fc07647ea07e6c24eeb7c \
                    file://COPYING_CCBYSA3;md5=96143d33de3a79321b1006c4e8ed07e7 \
                    file://COPYING_LGPL;md5=e6a600fd5e1d9cbde2d983680233ad02"

inherit allarch autotools pkgconfig gettext gtk-icon-cache gnomebase

SRC_URI += " \
           file://0001-Don-t-use-AC_CANONICAL_HOST.patch \
           file://0001-Run-installation-commands-as-shell-jobs.patch \
           "

SRC_URI[archive.sha256sum] = "ef5339d8c35fcad5d10481b70480803f0fa20b3d3cbc339238fcaceeaee01eba"

DEPENDS += "librsvg-native"

PACKAGES = "${PN}-cursors ${PN}-symbolic-hires ${PN}-symbolic ${PN}-hires ${PN}"

RREPLACES:${PN} = "gnome-icon-theme"
RCONFLICTS:${PN} = "gnome-icon-theme"
RPROVIDES:${PN} = "gnome-icon-theme"

FILES:${PN}-cursors = "${prefix}/share/icons/Adwaita/cursors/"
FILES:${PN}-symbolic-hires = "${prefix}/share/icons/Adwaita/96x96/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/64x64/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/48x48/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/32x32/*/*.symbolic.png"
FILES:${PN}-symbolic = "${prefix}/share/icons/Adwaita/16x16/*/*.symbolic.png \
                        ${prefix}/share/icons/Adwaita/24x24/*/*.symbolic.png \
                        ${prefix}/share/icons/Adwaita/scalable/*/*-symbolic*.svg"
FILES:${PN}-hires = "${prefix}/share/icons/Adwaita/256x256/ \
                     ${prefix}/share/icons/Adwaita/512x512/"
FILES:${PN} = "${prefix}/share/icons/Adwaita/ \
               ${prefix}/share/pkgconfig/adwaita-icon-theme.pc"

BBCLASSEXTEND = "native nativesdk"
