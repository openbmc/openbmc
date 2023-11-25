SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "4dcfa4b13a55685c49d3d17c10631eca18c33770f66316f8ef2337b8951cc144"

DEPENDS = "systemd python3-cython-native"
RDEPENDS:${PN} += "\
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-io \
"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi setuptools3 features_check pkgconfig

do_configure:prepend() {
    rm -rf ${S}/pystemd/*.c
}

BBCLASSEXTEND = "native"
