SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[md5sum] = "1e6aabe30f4c86356d1d85a9beb0b05a"
SRC_URI[sha256sum] = "5cf36eb6b1dc62d55f3c64289792cbaebc8ffa5a9da14474f49b46d20caa7fc8"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
