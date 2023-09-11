SUMMARY = "Modern Python library for the systemd D-Bus"
HOMEPAGE = "https://python-sdbus.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e77986dc8e2ee22d44a7c863e96852ae"

SRC_URI[sha256sum] = "f86fbadae54fea6441ec2f27dc29daf085269d66c5d9df1a4fbc9474a24b91d0"

REQUIRED_DISTRO_FEATURES = "systemd"
DEPENDS += "systemd"

inherit pypi setuptools3 features_check pkgconfig
