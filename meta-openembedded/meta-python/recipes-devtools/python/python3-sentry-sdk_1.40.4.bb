SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03dc788d9a9486be5e6a1d99c2c1ce3a"

RDEPENDS:${PN} += "\
	python3-urllib3 \
	python3-core \
	python3-json \
	python3-logging \
	python3-threading \
	python3-compression \
	python3-datetime \
"

SRC_URI[sha256sum] = "657abae98b0050a0316f0873d7149f951574ae6212f71d2e3a1c4c88f62d6456"

PYPI_PACKAGE = "sentry-sdk"

inherit pypi setuptools3
