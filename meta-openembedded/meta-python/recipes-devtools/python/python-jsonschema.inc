SUMMARY = "An implementation of JSON Schema validation for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8"

SRC_URI[md5sum] = "a802ab85600074a726ef6acc4e6a8148"
SRC_URI[sha256sum] = "2fa0684276b6333ff3c0b1b27081f4b2305f0a36cf702a23db50edb141893c3f"

DEPENDS += "${PYTHON_PN}-vcversioner-native ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-importlib-metadata \
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
