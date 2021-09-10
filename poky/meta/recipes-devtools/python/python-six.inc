SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://pypi.org/project/six/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=43cfc9e4ac0e377acfb9b76f56b8415d"

inherit pypi

RDEPENDS:${PN} = "${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
