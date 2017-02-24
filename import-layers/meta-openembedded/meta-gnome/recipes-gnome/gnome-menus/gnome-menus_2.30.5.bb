SUMMARY = "GNOME menus"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"
PR = "r1"

DEPENDS = "python libxml2 gconf popt gtk+ intltool-native"

inherit gnomebase pkgconfig python-dir pythonnative gobject-introspection

SRC_URI[archive.md5sum] = "caa6772e63ed5870cf43dc3d354e0624"
SRC_URI[archive.sha256sum] = "6dcc565006d6e8c2025ae83ab1f82edf6bd04d61c804c0dc9bf5ea50629c4caa"
GNOME_COMPRESS_TYPE="bz2"

do_configure_append () {

    # this expands to native-pthon which we don't want.
    sed -i 's:\@PYTHON\@:/usr/bin/env python:' ${S}/simple-editor/gmenu-simple-editor.in

}
PACKAGES += "${PN}-python ${PN}-python-staticdev"
FILES_${PN} += "${datadir}/desktop-directories/"
FILES_${PN}-python-staticdev = "${PYTHON_SITEPACKAGES_DIR}/gmenu.a ${PYTHON_SITEPACKAGES_DIR}/gmenu.la"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/gmenu.so ${PYTHON_SITEPACKAGES_DIR}/GMenuSimpleEditor "
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*/.debug \
                    ${PYTHON_SITEPACKAGES_DIR}/.debug"

# http://errors.yoctoproject.org/Errors/Details/81001/
PNBLACKLIST[gnome-menus] ?= "BROKEN: fails to build because of python"
