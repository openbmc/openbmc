SUMMARY = "The Swiss Army knife of Python web development"
DESCRIPTION = "\
Werkzeug started as simple collection of various utilities for WSGI \
applications and has become one of the most advanced WSGI utility modules. \
It includes a powerful debugger, full featured request and response objects, \
HTTP utilities to handle entity tags, cache control headers, HTTP dates, \
cookie handling, file uploads, a powerful URL routing system and a bunch \
of community contributed addon modules."
HOMEPAGE = "http://werkzeug.pocoo.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed9a0b7c0254616d72dadb98b293ef1b"

SRC_URI[md5sum] = "6d20b5be2d245be4ac7706cc390d130c"
SRC_URI[sha256sum] = "c3fd7a7d41976d9f44db327260e263132466836cef6f91512889ed60ad26557c"

PYPI_PACKAGE = "Werkzeug"

CLEANBROKEN = "1"

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests+= " \
    ${PYTHON_SITEPACKAGES_DIR}/werkzeug/test* \
    ${PYTHON_SITEPACKAGES_DIR}/werkzeug/__pycache__/test* \
    ${PYTHON_SITEPACKAGES_DIR}/werkzeug/contrib/test* \
    ${PYTHON_SITEPACKAGES_DIR}/werkzeug/contrib/__pycache__/test* \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-difflib \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unixadmin \
"

RDEPENDS_${PN}-tests = " \
    ${PN} \
    ${PYTHON_PN}-unittest \
"
