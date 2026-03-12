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
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[sha256sum] = "210c6bede5a420a913956b4791a7f4d6843a43b6fcee4dfa08a65e93007d0d25"

CVE_PRODUCT = "werkzeug"

inherit pypi python_flit_core

RDEPENDS:${PN} += " \
    python3-markupsafe \
    python3-logging \
    python3-profile \
    python3-compression \
    python3-json \
    python3-difflib \
"
