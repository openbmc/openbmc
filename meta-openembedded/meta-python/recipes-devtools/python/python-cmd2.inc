SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01aeabea7ae1529a8e4b674b7107d6bc"

SRC_URI[md5sum] = "90dccab3a20106c543ef85d2bee720bb"
SRC_URI[sha256sum] = "ba244b07c0b465ff54a6838dc61919599141dc92de1bf00bb0a70875189155e6"

inherit pypi

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
