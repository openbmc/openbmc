SUMMARY = "Backup, restore, import, and export panel layouts"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-panel-profiles/start"
SECTION = "x11/application"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext gtk-icon-cache features_check

REQUIRED_DISTRO_FEATURES = "x11 gobject-introspection-data"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.1/${BP}.tar.xz"
SRC_URI[sha256sum] = "0126373a03778bb4894afa151de28d36bfc563ddab96d3bd7c63962350d34ba2"

do_configure() {
    # special configure - no autotools...
    ./configure --prefix=${prefix}
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
    sed -i 's:${PYTHON}:python3:g' ${D}${bindir}/xfce4-panel-profiles
}

FILES:${PN} += "${datadir}/metainfo"

RDEPENDS:${PN} += "python3-pygobject python3-pexpect python3-psutil"
