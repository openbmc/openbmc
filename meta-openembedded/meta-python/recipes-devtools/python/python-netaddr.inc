SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[md5sum] = "51019ef59c93f3979bcb37d3b8527e07"
SRC_URI[sha256sum] = "38aeec7cdd035081d3a4c306394b19d677623bf76fa0913f6695127c7753aefd"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-xml \
"
