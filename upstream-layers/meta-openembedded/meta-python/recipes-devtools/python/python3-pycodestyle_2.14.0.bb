SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "c4b5b517d278089ff9d0abdec919cd97262a3367449ea1c8b49b91529167b783"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
