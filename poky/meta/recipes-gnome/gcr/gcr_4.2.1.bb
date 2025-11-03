SUMMARY = "A library for bits of crypto UI and parsing etc"
DESCRIPTION = "GCR is a library for displaying certificates, and crypto UI, \
accessing key stores. It also provides the viewer for crypto files on the \
GNOME desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcr"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gcr/issues"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "p11-kit glib-2.0 libgcrypt gnupg-native \
           ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'libxslt-native', '', d)}"

CACHED_CONFIGUREVARS += "ac_cv_path_GPG='gpg2'"

CFLAGS += "-D_GNU_SOURCE"

GTKDOC_MESON_OPTION = "gtk_doc"
inherit gnomebase gtk-icon-cache gi-docgen features_check vala gobject-introspection gettext mime mime-xdg

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'opengl', '', d)}"

SRC_URI[archive.sha256sum] = "ed783b5c80373cd058c02ea9e3e2a64e558599ca190a5abd598122e479967de5"

PACKAGECONFIG ??= " \
	${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'gtk', '', d)} \
	${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)} \
"
PACKAGECONFIG[gtk] = "-Dgtk4=true,-Dgtk4=false,gtk4"
PACKAGECONFIG[ssh_agent] = "-Dssh_agent=true,-Dssh_agent=false,libsecret,openssh"
#'Use systemd socket activation for server programs'
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false,"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-4 \
    ${systemd_user_unitdir}/gcr-ssh-agent.socket \
    ${systemd_user_unitdir}/gcr-ssh-agent.service \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gpg2 = '${bindir}/gpg2'
ssh-add = '${bindir}/ssh-add'
ssh-agent = '${bindir}/ssh-agent'
EOF
}
