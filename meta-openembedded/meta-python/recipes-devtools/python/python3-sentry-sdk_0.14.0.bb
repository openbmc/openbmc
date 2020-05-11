SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[md5sum] = "2d5cc43c8a178134b739c77439d1f26b"
SRC_URI[sha256sum] = "8e2d38dc58dc992280487e553ec3d97a424e4d179f4fad802ef3b08f64ccf4d8"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
