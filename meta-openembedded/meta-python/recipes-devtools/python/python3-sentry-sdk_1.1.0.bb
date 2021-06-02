SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

RDEPENDS_${PN} += "python3-urllib3"

SRC_URI[sha256sum] = "c1227d38dca315ba35182373f129c3e2722e8ed999e52584e6aca7d287870739"

PYPI_PACKAGE = "sentry-sdk"

inherit pypi setuptools3
