SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "4dcfa4b13a55685c49d3d17c10631eca18c33770f66316f8ef2337b8951cc144"

DEPENDS = "systemd"
RDEPENDS:${PN} += "\
    python3-xml \
    python3-lxml \
    python3-pprint \
    python3-core \
    python3-shell \
    python3-io \
"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi python_setuptools_build_meta features_check pkgconfig cython

do_configure:prepend() {
    rm -rf ${S}/pystemd/*.c
}

BBCLASSEXTEND = "native"
