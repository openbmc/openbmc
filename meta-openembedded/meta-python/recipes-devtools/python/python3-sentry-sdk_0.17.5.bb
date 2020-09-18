SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[md5sum] = "dcdf0d4f84f4f6ea02ad3f15dfcff2d9"
SRC_URI[sha256sum] = "02f2a72698453f722b102562eb6430d2a82d6c6c40f2b991ed69e7628142de6a"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
