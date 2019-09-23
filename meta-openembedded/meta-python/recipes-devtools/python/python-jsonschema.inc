SUMMARY = "An implementation of JSON Schema validation for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8"

SRC_URI[md5sum] = "2b44d00dccc96d7ba3839190263152eb"
SRC_URI[sha256sum] = "8d4a2b7b6c2237e0199c8ea1a6d3e05bf118e289ae2b9d7ba444182a2959560d"

DEPENDS += "${PYTHON_PN}-vcversioner-native ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-pyrsistent \
    ${PYTHON_PN}-rfc3987 \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-strict-rfc3339 \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-setuptools-scm \
"

BBCLASSEXTEND = "native nativesdk"
