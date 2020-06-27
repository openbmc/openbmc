SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[md5sum] = "f254d6b61e07123f9e2643b1dcbd03df"
SRC_URI[sha256sum] = "d09252e5aec3913815d77eb8e8ea8fa6eb33521253e52f977f6abaa964776f3e"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-xml \
"
