SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

RDEPENDS:${PN} += "python3-urllib3"

SRC_URI[sha256sum] = "259535ba66933eacf85ab46524188c84dcb4c39f40348455ce15e2c0aca68863"

PYPI_PACKAGE = "sentry-sdk"

inherit pypi setuptools3
