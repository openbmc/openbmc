SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f81c84f9a053e31fe9402a2a4e78864"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "8d553ab33cc1e5d3da60b8be5224355fb28cde863cc8935c40f011a8c0d72ad4"

RDEPENDS:${PN} += " \
    python3-json \
    python3-jsonpointer \
    python3-netclient \
    python3-stringold \
"
