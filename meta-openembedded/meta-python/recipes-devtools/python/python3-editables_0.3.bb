SUMMARY = "A Python library for creating editable wheels"
HOMEPAGE = "https://github.com/pfmoore/editables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=41bc1be47b7bb8240db3ef928c7cb0bf"

SRC_URI[sha256sum] = "167524e377358ed1f1374e61c268f0d7a4bf7dbd046c656f7b410cde16161b1a"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
