SUMMARY = "Modern Python library for the systemd D-Bus"
HOMEPAGE = "https://python-sdbus.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e77986dc8e2ee22d44a7c863e96852ae"

SRC_URI[sha256sum] = "4ec4fa4108629bae5ab2775e5575b0251fdb555d4a3613c9ddb37fc73f882983"

REQUIRED_DISTRO_FEATURES = "systemd"
DEPENDS += "systemd"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-numbers \
    python3-core \
"

inherit pypi setuptools3 features_check pkgconfig
