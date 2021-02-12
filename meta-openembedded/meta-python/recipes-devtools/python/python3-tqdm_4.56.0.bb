SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[sha256sum] = "fe3d08dd00a526850568d542ff9de9bbc2a09a791da3c334f3213d8d0bbbca65"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
