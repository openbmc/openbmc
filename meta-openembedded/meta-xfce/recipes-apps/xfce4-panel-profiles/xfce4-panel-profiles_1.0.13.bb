SUMMARY = "Backup, restore, import, and export panel layouts"
SECTION = "x11/application"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext gtk-icon-cache features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BP}.tar.bz2 \
           file://not-create-link-to-locale.patch \
           "
SRC_URI[sha256sum] = "bc387c13f94109422dc72b0fcb919b0dc11619ba589d03e492252b0d2513b170"

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
