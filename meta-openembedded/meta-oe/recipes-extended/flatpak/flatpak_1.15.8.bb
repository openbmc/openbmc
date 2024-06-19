DESCRIPTION = "Desktop containment framework."
HOMEPAGE = "http://flatpak.org"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = " \
    gitsm://github.com/flatpak/flatpak;protocol=https;branch=main \
    file://0001-flatpak-pc-add-pc_sysrootdir.patch \
"

SRCREV = "925c80f913d69e7ca424428823e1431c4ffb0deb"

S = "${WORKDIR}/git"

inherit meson pkgconfig gettext systemd gtk-doc gobject-introspection python3native useradd mime features_check

REQUIRED_DISTRO_FEATURES = "polkit"

DEPENDS = " \
    appstream \
    bison-native \
    dconf \
    fuse3 \
    gdk-pixbuf \
    glib-2.0 \
    gpgme \
    json-glib \
    libarchive \
    libcap \
    libxml2 \
    ostree \
    polkit \
    python3-pyparsing-native \
    zstd \
"

RDEPENDS:${PN} = " \
    ca-certificates \
    dconf \
    flatpak-xdg-utils \
    fuse3-utils \
"

GIR_MESON_OPTION = "gir"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtkdoc'
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG[curl] = "-Dhttp_backend=curl,,curl"
PACKAGECONFIG[docbook_docs] = "-Ddocbook_docs=enabled,-Ddocbook_docs=disabled,xmlto-native"
PACKAGECONFIG[man] = "-Dman=enabled,-Dman=disabled,libxslt-native"
PACKAGECONFIG[soup] = "-Dhttp_backend=soup,,libsoup-2.4"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,xauth socat-native"
PACKAGECONFIG[xauth] = "-Dxauth=enabled,-Dxauth=disabled,xauth"
PACKAGECONFIG[seccomp] = "-Dseccomp=enabled,-Dseccomp=disabled,libseccomp"
PACKAGECONFIG[malcontent] = "-Dmalcontent=enabled,-Dmalcontent=disabled,malcontent"
PACKAGECONFIG[selinux] = "-Dselinux_module=enabled,-Dselinux_module=disabled,libselinux"
PACKAGECONFIG[wayland-security-context] = "-Dwayland_security_context=enabled,-Dwayland_security_context=disabled,wayland wayland-native wayland-protocols"

PACKAGECONFIG ?= " \
    curl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xauth', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', 'seccomp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-security-context', '', d)} \
"

EXTRA_OEMESON = "-Dsystem_fusermount=${bindir}/fusermount3"

FILES:${PN} += "${libdir} ${datadir}"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd"

do_install:append() {
    chmod 0700 ${D}/${datadir}/polkit-1/rules.d
    chown polkitd ${D}/${datadir}/polkit-1/rules.d
    chgrp root ${D}/${datadir}/polkit-1/rules.d
}
