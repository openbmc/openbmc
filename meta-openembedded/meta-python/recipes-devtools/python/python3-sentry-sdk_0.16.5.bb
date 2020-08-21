SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[md5sum] = "6d65fda758929b6a8d16e6030302c811"
SRC_URI[sha256sum] = "e12eb1c2c01cd9e9cfe70608dbda4ef451f37ef0b7cbb92e5d43f87c341d6334"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
