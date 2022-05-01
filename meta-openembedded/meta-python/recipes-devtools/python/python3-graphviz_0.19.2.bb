DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=951dd0868a9606c867ffda0ea3ea6da2"

SRC_URI[sha256sum] = "7c90cebc147c18bcdffcd3c76db58cbface5d45fe0247a2f3bfb144d32a8c77c"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
