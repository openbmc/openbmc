DESCRIPTION = "An advanced menu editor"
HOMEPAGE = "https://launchpad.net/menulibre"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    python3-distutils-extra-native \
    intltool-native \
"

inherit distutils3 gtk-icon-cache distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://launchpad.net/menulibre/2.1/${PV}/+download/${PN}-${PV}.tar.gz"
SRC_URI[md5sum] = "efc7edb49bb0e5fea49e158b40573334"
SRC_URI[sha256sum] = "ef05b2722bab2acb7070d6c8ed0e7bd58bd4a4540bf498af9e889944f9da08b5"

do_compile() {
}

do_install_append() {
    sed -i 's:${D}::g' ${D}${datadir}/applications/menulibre.desktop
    sed -i 's:share/share:share:g' ${D}${PYTHON_SITEPACKAGES_DIR}/menulibre_lib/menulibreconfig.py
}

FILES_${PN} += " \
    ${datadir}/applications \
    ${datadir}/menulibre \
    ${datadir}/icons \
"

RDEPENDS_${PN} += " \
    gtk+3 \
    gtksourceview3 \
    python3-pygobject \
    gnome-menus3 \
    python3-unixadmin \
    python3-psutil \
"
