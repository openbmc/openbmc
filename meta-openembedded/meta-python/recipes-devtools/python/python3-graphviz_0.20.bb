DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=951dd0868a9606c867ffda0ea3ea6da2"

SRC_URI[sha256sum] = "76bdfb73f42e72564ffe9c7299482f9d72f8e6cb8d54bce7b48ab323755e9ba5"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
