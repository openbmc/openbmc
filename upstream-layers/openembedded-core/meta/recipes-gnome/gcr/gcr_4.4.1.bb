SUMMARY = "A library for bits of crypto UI and parsing etc"
DESCRIPTION = "GCR is a library for displaying certificates, and crypto UI, \
accessing key stores. It also provides the viewer for crypto files on the \
GNOME desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcr"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gcr/issues"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "p11-kit glib-2.0 libgcrypt"

CFLAGS += "-D_GNU_SOURCE"

GTKDOC_MESON_OPTION = "gtk_doc"
inherit gnomebase gi-docgen vala gobject-introspection lib_package
UPSTREAM_CHECK_REGEX = "gcr-(?P<pver>\d+\.\d+\.(?!9\d+)\d+(\.\d+)?)"

SRC_URI[archive.sha256sum] = "c4442c15d4330f17a1f5194df08c576877af68412ab2521446a93bd5e24c931b"

PACKAGECONFIG ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)}"
PACKAGECONFIG[ssh_agent] = "-Dssh_agent=true,-Dssh_agent=false,libsecret"
# Socket activation for the ssh-agent
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false,"
# A tool to view certificates
PACKAGECONFIG[viewer] = "-Dgtk4=true,-Dgtk4=false,gtk4"

PACKAGE_BEFORE_PN += "${PN}-ssh-agent"
FILES:${PN}-ssh-agent = "${libexecdir}/gcr-ssh-agent ${systemd_user_unitdir}/gcr-ssh-agent.*"
RDEPENDS:${PN}-ssh-agent += "openssh"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gpg = '${bindir}/gpg'
ssh-add = '${bindir}/ssh-add'
ssh-agent = '${bindir}/ssh-agent'
EOF
}
