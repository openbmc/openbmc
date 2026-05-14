SUMMARY = "A purl aka. Package URL parser and builder"
HOMEPAGE = "https://github.com/package-url/packageurl-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://mit.LICENSE;md5=ec425c78d8beabdb209b01c5fbcd38e0"

SRC_URI[sha256sum] = "1252ce3a102372ca6f86eb968e16f9014c4ba511c5c37d95a7f023e2ca6e5c25"

inherit pypi setuptools3

PYPI_PACKAGE = "packageurl_python"

BBCLASSEXTEND = "native nativesdk"
