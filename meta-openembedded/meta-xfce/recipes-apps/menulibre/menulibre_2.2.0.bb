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

SRC_URI = "https://launchpad.net/menulibre/2.2/${PV}/+download/${PN}-${PV}.tar.gz"
SRC_URI[md5sum] = "9690848dbf9196c3f7baa574e90378d8"
SRC_URI[sha256sum] = "546fec87f451c8f5c220d02b1f36dd850f4ce9ab607f8e9711ed8f2009e349fe"

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
