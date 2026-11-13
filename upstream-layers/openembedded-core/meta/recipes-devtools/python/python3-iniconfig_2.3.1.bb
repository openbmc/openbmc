SUMMARY = "A small and simple INI-file parser module"
HOMEPAGE = "https://pypi.org/project/iniconfig/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a73af4b5f1f3a8731e5def70e8c1254"

SRC_URI[sha256sum] = "67f4b9c50da0dedf52af349e7749a80a9057a5031199791b906c3bb3ae878960"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
