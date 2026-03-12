SUMMARY = "This provides screenshot/screencast xdg-desktop-portal backends for wlroots."
HOMEPAGE = "https://github.com/emersion/xdg-desktop-portal-wlr"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e316e9609dd7672b87ff25b46b2cf3e1"

DEPENDS = " \
    wayland \
    wayland-native \
    wayland-protocols \
    libdrm \
    libinih \
    pipewire \
    virtual/libgbm \
"

inherit meson pkgconfig features_check manpages
REQUIRED_DISTRO_FEATURES = "opengl wayland"

SRC_URI = "git://github.com/emersion/xdg-desktop-portal-wlr.git;protocol=https;branch=master"

PV .= "+git"
SRCREV = "256eb4a9f6ecffd64289d6501d036c2094b47db6"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'basu', d)}"

PACKAGECONFIG[manpages] = "-Dman-pages=enabled,-Dman-pages=disabled,scdoc-native"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled -Dsd-bus-provider=libsystemd,-Dsystemd=disabled"
PACKAGECONFIG[basu] = "-Dsd-bus-provider=basu,,basu"

FILES:${PN} += "${systemd_user_unitdir} ${datadir}"
