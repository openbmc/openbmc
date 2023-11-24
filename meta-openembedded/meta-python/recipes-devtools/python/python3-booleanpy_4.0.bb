SUMMARY = "Define boolean algebras, create and parse boolean expressions and create custom boolean DSL"
HOMEPAGE = "https://github.com/bastikr/boolean.py"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d118b5feceee598ebeca76e13395c2bd"

SRC_URI[sha256sum] = "17b9a181630e43dde1851d42bef546d616d5d9b4480357514597e78b203d06e4"

PYPI_PACKAGE = "boolean.py"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
