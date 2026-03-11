SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "f06d4e5b21881a7c9100492e60ccc2f4b64e50b4d6b7773c94013d2c6f1a00db"

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
