SUMMARY = "The Swiss Army knife of Python web development"
DESCRIPTION = "\
Werkzeug started as simple collection of various utilities for WSGI \
applications and has become one of the most advanced WSGI utility modules. \
It includes a powerful debugger, full featured request and response objects, \
HTTP utilities to handle entity tags, cache control headers, HTTP dates, \
cookie handling, file uploads, a powerful URL routing system and a bunch \
of community contributed addon modules."
HOMEPAGE = "http://werkzeug.pocoo.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[md5sum] = "5b23b4953efc4f52b1d0b33af6f7cd2d"
SRC_URI[sha256sum] = "7280924747b5733b246fe23972186c6b348f9ae29724135a6dfc1e53cea433e7"

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
