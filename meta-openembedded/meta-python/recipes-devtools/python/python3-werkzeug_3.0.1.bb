SUMMARY = "The comprehensive WSGI web application library"
DESCRIPTION = "\
Werkzeug started as simple collection of various utilities for WSGI \
applications and has become one of the most advanced WSGI utility modules. \
It includes a powerful debugger, full featured request and response objects, \
HTTP utilities to handle entity tags, cache control headers, HTTP dates, \
cookie handling, file uploads, a powerful URL routing system and a bunch \
of community contributed addon modules."
HOMEPAGE = "https://werkzeug.palletsprojects.com"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[sha256sum] = "507e811ecea72b18a404947aded4b3390e1db8f826b494d76550ef45bb3b1dcc"

inherit pypi python_flit_core

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-markupsafe \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-json \
"
