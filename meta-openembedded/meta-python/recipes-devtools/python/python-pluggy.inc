SUMMARY = "Plugin and hook calling mechanisms for python"
HOMEPAGE = "https://github.com/pytest-dev/pluggy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c8206d16fd5cc02fa9b0bb98955e5c2"

SRC_URI[md5sum] = "4b8384b69cddbc2e104fdd3ff5631788"
SRC_URI[sha256sum] = "fa5fa1622fa6dd5c030e9cad086fa19ef6a0cf6d7a2d12318e10cb49d6d68f34"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi
