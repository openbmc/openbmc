DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

inherit pypi setuptools3

SRC_URI[sha256sum] = "9dcbf1808898f440aed304a5e9f0742a2859eca3b0ac7f1f58e50502852a8ef9"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-sortedcontainers \
"

BBCLASSEXTEND = "native"
