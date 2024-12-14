SUMMARY = "Raven is the legacy Python client for Sentry (getsentry.com)"
DESCRIPTION = "\
Raven is the official legacy Python client for Sentry, officially \
supports Python 2.6–2.7 & 3.3–3.7, and runs on PyPy and Google App Engine."
HOMEPAGE = "https://github.com/getsentry/raven-python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b9a4414e08f0571d55184531cefc131b"

SRC_URI[sha256sum] = "3fa6de6efa2493a7c827472e984ce9b020797d0da16f1db67197bcc23c8fae54"

PYPI_PACKAGE = "raven"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-logging \
"
