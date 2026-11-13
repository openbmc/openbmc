SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03dc788d9a9486be5e6a1d99c2c1ce3a"

RDEPENDS:${PN} += " \
    python3-urllib3 \
    python3-core \
    python3-json \
    python3-logging \
    python3-threading \
    python3-compression \
    python3-datetime \
    python3-certifi \
"

SRC_URI[sha256sum] = "4a2ab97b16e4693c325ab361c7bbbb346dbc7d0bc0150922d795a25bac8d5280"

inherit pypi setuptools3
