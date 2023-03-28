DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

inherit pypi setuptools3

SRC_URI[sha256sum] = "247471718131d41fb82137ab7b6466cdf4b785d047e38d309ebf34c84101a3a6"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-sortedcontainers \
"

BBCLASSEXTEND = "native"
