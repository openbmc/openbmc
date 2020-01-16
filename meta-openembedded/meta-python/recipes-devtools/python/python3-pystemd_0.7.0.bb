SUMMARY = "Python bindings for interacting with systemd over DBus"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[md5sum] = "f493c3e54457e49fe3c160274b863bac"
SRC_URI[sha256sum] = "f5dc49d02995ab96335d9e94f9fe036673d89b8cc9907e7a0ac83c06665f2430"

DEPENDS = "systemd"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit pypi setuptools3 features_check

BBCLASSEXTEND = "native"
