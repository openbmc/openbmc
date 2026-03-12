SUMMARY = "Modern Python library for the systemd D-Bus"
HOMEPAGE = "https://python-sdbus.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e77986dc8e2ee22d44a7c863e96852ae"

SRC_URI[sha256sum] = "4f5d13b196e1e1de35311ebb2563a32de791451a30f5b9a4894528ba98766412"

REQUIRED_DISTRO_FEATURES = "systemd"
DEPENDS += "systemd"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-numbers \
    python3-core \
"

inherit pypi setuptools3 features_check pkgconfig
