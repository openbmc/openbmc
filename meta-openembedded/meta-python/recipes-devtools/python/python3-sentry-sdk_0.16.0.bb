SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0c79f8d3c91fc847350efd28bfe0a341"

SRC_URI[md5sum] = "8cfa879e803add117b073cad20c1d74d"
SRC_URI[sha256sum] = "da06bc3641e81ec2c942f87a0676cd9180044fa3d1697524a0005345997542e2"

PYPI_PACKAGE = "sentry-sdk"
inherit pypi setuptools3
