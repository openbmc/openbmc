SUMMARY = "libqmi is a library for talking to WWAN devices by QMI protocol"
DESCRIPTION = "libqmi is a glib-based library for talking to WWAN modems and \
               devices which speak the Qualcomm MSM Interface (QMI) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libqmi"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0 glib-2.0-native"

inherit meson pkgconfig bash-completion gobject-introspection

SRCREV ?= "f6c2feaf199e0f129a0bde8c5e6cba5f2117b564"
# patch 0001 is on main branch upstream
SRC_URI = "\
    git://gitlab.freedesktop.org/mobile-broadband/libqmi.git;protocol=https;branch=qmi-1-30 \
    file://0001-build-use-python3-everywhere.patch \
"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "udev mbim"
PACKAGECONFIG[udev] = "-Dudev=true,-Dudev=false,libgudev"
PACKAGECONFIG[mbim] = "-Dmbim_qmux=true,-Dmbim_qmux=false,libmbim"
PACKAGECONFIG[qrtr] = "-Dqrtr=true,-Dqrtr=false,libqrtr-glib"

EXTRA_OEMESON = " \
    -Dgtk_doc=false \
    -Dman=false \
"
