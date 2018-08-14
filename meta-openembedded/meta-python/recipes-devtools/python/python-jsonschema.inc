SUMMARY = "An implementation of JSON Schema validation for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8"

SRC_URI[md5sum] = "50c6b69a373a8b55ff1e0ec6e78f13f4"
SRC_URI[sha256sum] = "6ff5f3180870836cae40f06fa10419f557208175f13ad7bc26caa77beb1f6e02"

DEPENDS += "${PYTHON_PN}-vcversioner-native"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-rfc3987 \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-strict-rfc3339 \
    ${PYTHON_PN}-unittest \
"

BBCLASSEXTEND = "native nativesdk"
