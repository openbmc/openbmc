SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "d74a814bfda01085db1a8ad90be3cb27daf23a51ab6b03e7e29ec811fa2ae859"

DEPENDS = "systemd python3-cython-native"
RDEPENDS:${PN} += "python3-xml python3-pprint"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi setuptools3 features_check pkgconfig

do_configure:prepend() {
    rm -rf ${S}/pystemd/*.c
}

BBCLASSEXTEND = "native"
