SUMMARY = "Backup, restore, import, and export panel layouts"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "bc75a143423fba3a82f114f47e86580c"
SRC_URI[sha256sum] = "a69e20f5e637319e14898b5c13ff7ba31d001a6e38e7516d70dbfd7600ad72db"

do_configure() {
    # special configure - no autotools...
    ./configure --prefix=${prefix}
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
    sed -i 's:${PYTHON}:python3:g' ${D}${bindir}/xfce4-panel-profiles
}

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject python3-pexpect"
