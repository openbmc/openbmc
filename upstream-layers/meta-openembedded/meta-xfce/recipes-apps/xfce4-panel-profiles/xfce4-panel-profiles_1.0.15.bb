SUMMARY = "Backup, restore, import, and export panel layouts"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-panel-profiles/start"
SECTION = "x11/application"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext gtk-icon-cache features_check

REQUIRED_DISTRO_FEATURES = "x11 gobject-introspection-data"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BP}.tar.bz2"
SRC_URI[sha256sum] = "56cce1a27e88a18a282d568cbc601547a5dd704f0449a75bc284f0171aebaf3b"

do_configure() {
    # special configure - no autotools...
    ./configure --prefix=${prefix}
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
    sed -i 's:${PYTHON}:python3:g' ${D}${bindir}/xfce4-panel-profiles
}

FILES:${PN} += "${datadir}/metainfo"

RDEPENDS:${PN} += "python3-pygobject python3-pexpect"
