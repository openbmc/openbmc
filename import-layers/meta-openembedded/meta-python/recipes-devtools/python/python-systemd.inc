SUMMARY = "Python interface for libsystemd"
HOMEPAGE = "https://github.com/systemd/python-systemd"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4fbd65380cdd255951079008b364516c"

PYPI_PACKAGE = "systemd-python"
DEPENDS += "systemd (<=234)"
RDEPENDS_${PN} += "systemd ${PYTHON_PN}-syslog ${PYTHON_PN}-logging"
REQUIRED_DISTRO_FEATURES = "systemd"
inherit pypi distro_features_check

SRC_URI[md5sum] = "5071ea5bcb976186e92a3f5e75df221d"
SRC_URI[sha256sum] = "fd0e44bf70eadae45aadc292cb0a7eb5b0b6372cd1b391228047d33895db83e7"

# allow for common patches for python- and python3-systemd
FILESEXTRAPATHS_prepend := "${THISDIR}/python-systemd:"

SRC_URI_append_libc-musl = " file://0001-Provide-implementation-of-strndupa-for-musl.patch"
