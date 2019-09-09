SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01aeabea7ae1529a8e4b674b7107d6bc"

SRC_URI[md5sum] = "df35bb2dca8f5e1f6090e1f0aa02960a"
SRC_URI[sha256sum] = "4b78379d53aff811d1deac720bbe71661769822a5fb2d830cd730656d180fb3d"

inherit pypi

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
