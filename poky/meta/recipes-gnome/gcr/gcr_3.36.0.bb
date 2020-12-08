SUMMARY = "A library for bits of crypto UI and parsing etc"
DESCRIPTION = "GCR is a library for displaying certificates, and crypto UI, \
accessing key stores. It also provides the viewer for crypto files on the \
GNOME desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcr"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gcr/issues"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "gtk+3 p11-kit glib-2.0 libgcrypt gnupg-native \
           ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'libxslt-native', '', d)}"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
inherit gnomebase gtk-icon-cache gtk-doc features_check upstream-version-is-even vala gobject-introspection gettext mime mime-xdg
# depends on gtk+3, but also x11 through gtk+-x11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://0001-meson.build-correctly-set-internal-vapi-dependencies.patch"
SRC_URI += " file://0001-meson-Make-sure-gcr-oids.h-is-built.patch"

SRC_URI[archive.md5sum] = "adc65563b6b458507b9a578a8b68fb61"
SRC_URI[archive.sha256sum] = "aaf9bed017a2263c6145c89a1a84178f9f40f238426463e4ae486694ef5f6601"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-3 \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"
