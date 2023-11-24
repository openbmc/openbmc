SUMMARY = "libdecor - A client-side decorations library for Wayland clients"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = " \
    git://gitlab.freedesktop.org/libdecor/libdecor.git;protocol=https;branch=master \
"

DEPENDS = " \
    cairo \
    pango \
    wayland \
    wayland-native \
    wayland-protocols \
"

S = "${WORKDIR}/git"
SRCREV = "ad320fc0e0ec2cd75a87fed454a9c7d6d1921464"

PACKAGECONFIG ?= "dbus ${@bb.utils.filter('DISTRO_FEATURES', 'gtk+3', d)}"

PACKAGECONFIG[dbus] = "-Ddbus=enabled,-Ddbus=disabled,dbus"
PACKAGECONFIG[demo] = "-Ddemo=true,-Ddemo=false,virtual/libegl libxkbcommon"
PACKAGECONFIG[install_demo] = "-Dinstall_demo=true,-Dinstall_demo=false"
PACKAGECONFIG[gtk+3] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"

inherit meson pkgconfig

EXTRA_OEMESON += "--buildtype release"

BBCLASSEXTEND = "native nativesdk"
