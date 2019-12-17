SUMMARY = "An implementation of JSON Schema validation for Python"
HOMEPAGE = "https://github.com/Julian/jsonschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8 \
                    file://json/LICENSE;md5=9d4de43111d33570c8fe49b4cb0e01af"
DEPENDS += "${PYTHON_PN}-vcversioner-native ${PYTHON_PN}-setuptools-scm-native"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-jsonschema:"

SRC_URI_append = " file://0001-setup.cfg-add-non-GPL-format-option.patch"

SRC_URI[md5sum] = "a802ab85600074a726ef6acc4e6a8148"
SRC_URI[sha256sum] = "2fa0684276b6333ff3c0b1b27081f4b2305f0a36cf702a23db50edb141893c3f"

PACKAGECONFIG ??= "format"
PACKAGECONFIG[format] = ",,,\
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-jsonpointer \
    ${PYTHON_PN}-webcolors \
    ${PYTHON_PN}-rfc3987 \
    ${PYTHON_PN}-strict-rfc3339 \
"
PACKAGECONFIG[nongpl] = ",,,\
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-jsonpointer \
    ${PYTHON_PN}-webcolors \
    ${PYTHON_PN}-rfc3986-validator \
    ${PYTHON_PN}-rfc3339-validator \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-importlib-metadata \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-pyrsistent \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-setuptools-scm \
    ${PYTHON_PN}-zipp \
"

BBCLASSEXTEND = "native nativesdk"
