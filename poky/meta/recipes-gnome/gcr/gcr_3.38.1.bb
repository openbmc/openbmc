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

CACHED_CONFIGUREVARS += "ac_cv_path_GPG='gpg2'"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
inherit gnomebase gtk-icon-cache gtk-doc features_check upstream-version-is-even vala gobject-introspection gettext mime mime-xdg
# depends on gtk+3, but also x11 through gtk+-x11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "17fcaf9c4a93a65fb1c72b82643bb102c13344084687d5886ea66313868d9ec9"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-3 \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

EXTRA_OEMESON += "--cross-file ${WORKDIR}/meson-${PN}.cross"
do_write_config_append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gpg2 = '${bindir}/gpg2'
EOF
}
