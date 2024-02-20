SUMMARY = "library to use and manage the QRTR bus"
DESCRIPTION = "libqrtr-glib is a glib-based library to use and manage the QRTR (Qualcomm IPC Router) bus"
HOMEPAGE = "https://gitlab.freedesktop.org/mobile-broadband/libqrtr-glib"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://gitlab.freedesktop.org/mobile-broadband/libqrtr-glib.git;protocol=https;branch=qrtr-1-2"

PV = "1.2.2+git"
SRCREV = "8991f0e93713ebf4da48ae4f23940ead42f64c8c"

S = "${WORKDIR}/git"

inherit meson pkgconfig gobject-introspection

DEPENDS = "glib-2.0"

EXTRA_OEMESON = " \
    -Dgtk_doc=false \
"
