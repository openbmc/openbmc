SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app python_setuptools_build_meta gtk-icon-cache mime-xdg

DEPENDS += "python3-distutils-extra-native"

SRC_URI[sha256sum] = "e9a99a62d10981391508dd43f3cbfa2d50a69bd6b7d1eeef7d30ba4c673dcfda"

FILES:${PN} += "${datadir}/metainfo"

RDEPENDS:${PN} += "python3-pygobject python3-dbus"

do_install:append() {
    #
    # Until catfish upstream figures out a way to overcome this buildpath issue, we need to do such adjustments here.
    #
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' ${D}${datadir}/applications/org.xfce.Catfish.desktop
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' ${D}${PYTHON_SITEPACKAGES_DIR}/catfish_lib/catfishconfig.py
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/catfish_lib/__pycache__/catfishconfig.*.pyc
}
