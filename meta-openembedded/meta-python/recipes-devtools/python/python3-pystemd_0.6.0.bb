SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[md5sum] = "c5472be419f53f53f5da089ca8c981c0"
SRC_URI[sha256sum] = "b1fc072c87e3766711f64caf86fd633dca393e20c8a0a37a5058dd70a21d8a14"

DEPENDS = "systemd"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi setuptools3 distro_features_check

BBCLASSEXTEND = "native"
