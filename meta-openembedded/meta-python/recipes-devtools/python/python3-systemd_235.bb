SUMMARY = "Python interface for libsystemd"
HOMEPAGE = "https://github.com/systemd/python-systemd"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4fbd65380cdd255951079008b364516c"


PYPI_PACKAGE = "systemd-python"
SRC_URI:append:libc-musl = " file://0001-Provide-implementation-of-strndupa-for-musl.patch"
SRC_URI[sha256sum] = "4e57f39797fd5d9e2d22b8806a252d7c0106c936039d1e71c8c6b8008e695c0a"

DEPENDS += "systemd (<=235)"

inherit pypi features_check pkgconfig python_setuptools_build_meta

REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} += "systemd python3-syslog python3-logging python3-syslog"
