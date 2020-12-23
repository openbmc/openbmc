SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[md5sum] = "f993739eca645749f18d4ccfd4a0fbf3"
SRC_URI[sha256sum] = "cac2e42043ab28d43adf33dab493c6a3cf8a99794f824ae8af6d9cd6458b9972"

DEPENDS = "systemd"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi setuptools3 features_check

BBCLASSEXTEND = "native"
