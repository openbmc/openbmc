SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "347187bdb476329d98f695c213d7295a846d1152ff4fe9bacb8a9590b8ee7053"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
