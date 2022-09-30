DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

inherit pypi setuptools3

SRC_URI[sha256sum] = "0e9d42838099263201b25517e4c1bd57042b5fe44432d6df38cef72d84d1eb1f"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-sortedcontainers \
"

BBCLASSEXTEND = "native"
