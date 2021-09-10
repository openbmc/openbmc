SUMMARY = "Define boolean algebras, create and parse boolean expressions and create custom boolean DSL"
HOMEPAGE = "https://github.com/bastikr/boolean.py"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9b58494d4f385978ca5a7ef4f6abca53"

SRC_URI[md5sum] = "83ccc145ba74a585637124c8bc648333"
SRC_URI[sha256sum] = "cc24e20f985d60cd4a3a5a1c0956dd12611159d32a75081dabd0c9ab981acaa4"

PYPI_PACKAGE = "boolean.py"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
