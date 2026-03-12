SUMMARY = "A markup language for GTK user interface files."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/blueprint-compiler"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI = "git://gitlab.gnome.org/GNOME/blueprint-compiler;protocol=https;branch=main;tag=v${PV}"
SRCREV = "aa5298cc1677cf855bd61f9671607362bd203f4f"

inherit meson pkgconfig python3targetconfig

PACKAGES += "${PN}-python"

FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python = "python3-pygobject"

BBCLASSEXTEND = "native"
