SUMMARY = "Backup, restore, import, and export panel layouts"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit python3native gettext features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "intltool-native"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/1.0/${BP}.tar.bz2 \
           file://not-create-link-to-locale.patch \
           "
SRC_URI[md5sum] = "6190678bc701c197babcb2389ba46182"
SRC_URI[sha256sum] = "a84d5e748d53bc5da269954cc3ad7f5ac0c4f5813acfd3892ea6f9064f17fb68"

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
