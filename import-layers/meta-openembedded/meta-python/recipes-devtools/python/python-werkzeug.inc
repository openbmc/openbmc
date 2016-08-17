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
LIC_FILES_CHKSUM = "file://LICENSE;md5=a68f5361a2b2ca9fdf26b38aaecb6faa"

SRC_URI[md5sum] = "daf443a939459e12f14fd2e4658a26df"
SRC_URI[sha256sum] = "afdb04f183eef3b20827eb4520dcb75c1cd1f0ba7a7de3f463ff7a462eee8164"

PYPI_PACKAGE = "Werkzeug"

RDEPENDS_${PN} += " \
    python-difflib \
    python-email \
    python-html \
    python-json \
    python-netserver \
    python-numbers \
    python-pkgutil \
    python-subprocess \
    python-threading \
    python-unixadmin \
    python-zlib \
"
