SUMMARY = "Backup, restore, import, and export panel layouts"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BP}.tar.bz2"
SRC_URI[md5sum] = "bee3e251e45ade0ea349366461d6e200"
SRC_URI[sha256sum] = "a8c00af838e85d00600dbf442c8741aa21a332fbceba849e0820560630a6e0ce"

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
