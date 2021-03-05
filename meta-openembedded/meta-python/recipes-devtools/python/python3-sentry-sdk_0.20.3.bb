SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[sha256sum] = "4ae8d1ced6c67f1c8ea51d82a16721c166c489b76876c9f2c202b8a50334b237"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
