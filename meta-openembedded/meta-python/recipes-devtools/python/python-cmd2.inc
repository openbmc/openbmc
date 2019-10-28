SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01aeabea7ae1529a8e4b674b7107d6bc"

SRC_URI[md5sum] = "20f3f76b2aaf36a17372717b469ca969"
SRC_URI[sha256sum] = "c81284083d993af18b8fef57d89d854d49d051d4c2c8a8e12d0281e369ac3682"

inherit pypi

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
