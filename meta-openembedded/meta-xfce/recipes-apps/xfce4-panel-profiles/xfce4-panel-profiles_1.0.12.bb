SUMMARY = "Backup, restore, import, and export panel layouts"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext gtk-icon-cache features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BP}.tar.bz2 \
           file://not-create-link-to-locale.patch \
           file://0001-Makefile.in.in-remove-bashisms.patch \
           "
SRC_URI[sha256sum] = "246e459d2d2f3f524968440ed7fddb2a891567ebc05f10a800f7f5821b3452a7"

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
