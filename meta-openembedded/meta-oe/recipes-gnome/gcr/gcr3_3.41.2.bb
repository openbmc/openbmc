SUMMARY = "A library for bits of crypto UI and parsing etc"
DESCRIPTION = "GCR is a library for displaying certificates, and crypto UI, \
accessing key stores. It also provides the viewer for crypto files on the \
GNOME desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcr"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gcr/issues"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "p11-kit glib-2.0 libgcrypt gnupg-native \
           ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'libxslt-native', '', d)}"

CACHED_CONFIGUREVARS += "ac_cv_path_GPG='gpg2'"

CFLAGS += "-D_GNU_SOURCE"

GTKDOC_MESON_OPTION = "gtk_doc"
VALA_MESON_OPTION ?= ''

inherit gnomebase gtk-icon-cache gi-docgen features_check upstream-version-is-even vala gobject-introspection gettext mime mime-xdg
UPSTREAM_CHECK_REGEX = "[^\d\.](?P<pver>3.(?!9\d+)\d+(\.\d+)+)\.tar"

SRC_URI = "https://download.gnome.org/sources/gcr/3.41/gcr-${PV}.tar.xz;name=archive"
SRC_URI += "file://0001-meson.build-correctly-handle-disabled-ssh_agent-opti.patch"
SRC_URI[archive.sha256sum] = "bad10f3c553a0e1854649ab59c5b2434da22ca1a54ae6138f1f53961567e1ab7"

S = "${WORKDIR}/gcr-${PV}"

PACKAGECONFIG ??= " \
	${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'gtk', '', d)} \
"
PACKAGECONFIG[gtk] = "-Dgtk=true,-Dgtk=false,gtk+3"
PACKAGECONFIG[ssh_agent] = "-Dssh_agent=true,-Dssh_agent=false,libsecret,openssh"
#'Use systemd socket activation for server programs'
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-3 \
    ${datadir}/vala \
    ${systemd_user_unitdir}/gcr-ssh-agent.socket \
    ${systemd_user_unitdir}/gcr-ssh-agent.service \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

EXTRA_OEMESON += "--cross-file ${WORKDIR}/meson-${PN}.cross"
do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gpg2 = '${bindir}/gpg2'
ssh-add = '${bindir}/ssh-add'
ssh-agent = '${bindir}/ssh-agent'
EOF
}
