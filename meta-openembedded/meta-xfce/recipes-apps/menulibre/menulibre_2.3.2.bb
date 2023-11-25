DESCRIPTION = "An advanced menu editor"
HOMEPAGE = "https://bluesabre.org/menulibre/"
SECTION = "x11/graphics"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    python3-distutils-extra-native \
    intltool-native \
"

inherit setuptools3_legacy gtk-icon-cache features_check
REQUIRED_DISTRO_FEATURES = "x11 gobject-introspection-data"

SRC_URI = "git://github.com/bluesabre/menulibre.git;protocol=https;branch=master"
SRCREV = "94bef4060714fa65c7246c25b87410b52a149f94"
S = "${WORKDIR}/git"

do_compile[noexec] = "1"

do_install:append() {
    sed -i 's:${D}::g' ${D}${datadir}/applications/menulibre.desktop
    sed -i 's:share/share:share:g' ${D}${PYTHON_SITEPACKAGES_DIR}/menulibre_lib/menulibreconfig.py
}

FILES:${PN} += " \
    ${datadir}/applications \
    ${datadir}/metainfo \
    ${datadir}/icons \
"

RDEPENDS:${PN} += " \
    gtk+3 \
    gtksourceview3 \
    python3-pygobject \
    gnome-menus \
    python3-unixadmin \
    python3-psutil \
"
