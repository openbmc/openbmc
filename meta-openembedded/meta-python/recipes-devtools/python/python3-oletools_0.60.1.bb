SUMMARY = "Python tools to analyze security characteristics of MS Office and OLE files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d7dd4b0d1f7153322a546e89b5a0a632"

SRC_URI[sha256sum] = "67a796da4c4b8e2feb9a6b2495bef8798a3323a75512de4e5669d9dc9d1fae31"

inherit pypi setuptools3

PYPI_PACKAGE = "oletools"
PYPI_PACKAGE_EXT = "zip"
