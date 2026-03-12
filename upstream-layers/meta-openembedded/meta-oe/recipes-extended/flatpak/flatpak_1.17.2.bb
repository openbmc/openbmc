DESCRIPTION = "Desktop containment framework."
HOMEPAGE = "http://flatpak.org"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = " \
    git://github.com/flatpak/flatpak;protocol=https;branch=main;tag=${PV} \
    file://0001-flatpak-pc-add-pc_sysrootdir.patch \
"

SRCREV = "20599618d623630f7aa38d61a2bb81d86fb25f09"


inherit meson pkgconfig gettext systemd gtk-doc gobject-introspection python3native mime features_check useradd

REQUIRED_DISTRO_FEATURES = "polkit"

DEPENDS = " \
    appstream \
    bison-native \
    bubblewrap-native \
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
    xdg-dbus-proxy-native \
    zstd \
    curl \
"

RDEPENDS:${PN} = " \
    ca-certificates \
    flatpak-xdg-utils \
    fuse3-utils \
    bubblewrap \
    xdg-dbus-proxy \
"

GIR_MESON_OPTION = "gir"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtkdoc'
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG[dconf] = "-Ddconf=enabled,-Ddconf=disabled,dconf"
PACKAGECONFIG[docbook_docs] = "-Ddocbook_docs=enabled,-Ddocbook_docs=disabled,xmlto-native"
PACKAGECONFIG[man] = "-Dman=enabled,-Dman=disabled,libxslt-native"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,xauth socat-native"
PACKAGECONFIG[xauth] = "-Dxauth=enabled,-Dxauth=disabled,xauth"
PACKAGECONFIG[seccomp] = "-Dseccomp=enabled,-Dseccomp=disabled,libseccomp"
PACKAGECONFIG[malcontent] = "-Dmalcontent=enabled,-Dmalcontent=disabled,malcontent"
PACKAGECONFIG[selinux] = "-Dselinux_module=enabled,-Dselinux_module=disabled,libselinux"
PACKAGECONFIG[wayland-security-context] = "-Dwayland_security_context=enabled,-Dwayland_security_context=disabled,wayland wayland-native wayland-protocols"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xauth', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', 'seccomp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-security-context', '', d)} \
"

EXTRA_OEMESON = " \
    -Dsystem_fusermount=fusermount3 \
    -Dsystem_bubblewrap=bwrap \
    -Dsystem_dbus_proxy=xdg-dbus-proxy \
"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --shell /sbin/nologin flatpak"

FILES:${PN} += "${libdir} ${datadir}"
