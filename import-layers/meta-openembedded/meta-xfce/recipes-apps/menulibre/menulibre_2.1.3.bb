DESCRIPTION = "An advanced menu editor"
HOMEPAGE = "https://launchpad.net/menulibre"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "python3-distutils-extra-native intltool-native"

inherit distutils3 gtk-icon-cache

SRC_URI = " \
    https://launchpad.net/menulibre/2.1/${PV}/+download/${PN}-${PV}.tar.gz \
    file://0001-add_launcher-Exit-early-if-no-row-is-selected.patch \
    file://0002-setup.py-avoid-usr-share-share-paths.patch \
    file://0003-MenulibreXdg.py-fix-loading-of-desktop-files.patch \
"
SRC_URI[md5sum] = "19d9d3337322eb5513454bb8cdfb739b"
SRC_URI[sha256sum] = "bdd69740119902f1b1f8c7831155f4428403792a0a6c4287bcbb395c4e71fb31"

do_install_append() {
    sed -i 's:${D}::g' ${D}${datadir}/applications/menulibre.desktop
}

FILES_${PN} += " \
    ${datadir}/applications \
    ${datadir}/menulibre \
    ${datadir}/icons \
"

RDEPENDS_${PN} += " \
    gtk+3 \
    python3-pygobject \
    gnome-menus3 \
    python3-unixadmin \
    python3-psutil \
"
