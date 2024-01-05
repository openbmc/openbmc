SUMMARY = "Modern Python library for the systemd D-Bus"
HOMEPAGE = "https://python-sdbus.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e77986dc8e2ee22d44a7c863e96852ae"

SRC_URI[sha256sum] = "adb97718ce996bb308520682c50b1a13e606d65a6edb1c1967a15d2e570cb3b7"

REQUIRED_DISTRO_FEATURES = "systemd"
DEPENDS += "systemd"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-core \
"

inherit pypi setuptools3 features_check pkgconfig
