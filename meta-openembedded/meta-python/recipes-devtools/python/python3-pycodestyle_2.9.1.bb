SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "2c9607871d58c76354b697b42f5d57e1ada7d261c261efac224b664affdc5785"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
