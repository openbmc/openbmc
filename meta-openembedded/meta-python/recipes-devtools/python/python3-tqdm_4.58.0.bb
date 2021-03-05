SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=59e4271a933d33edfe60237db377a14b"

SRC_URI[sha256sum] = "c23ac707e8e8aabb825e4d91f8e17247f9cc14b0d64dd9e97be0781e9e525bba"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
