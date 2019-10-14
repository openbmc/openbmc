SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01aeabea7ae1529a8e4b674b7107d6bc"

SRC_URI[md5sum] = "f7a51dc68ad377ebc6a84408044dc1a2"
SRC_URI[sha256sum] = "840cdbf50e83761fea69ca5beadcba7569ab03734e3ee42b0dbf7f6f90e1213d"

inherit pypi

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
