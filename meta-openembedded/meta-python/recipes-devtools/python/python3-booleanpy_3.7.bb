SUMMARY = "Define boolean algebras, create and parse boolean expressions and create custom boolean DSL"
HOMEPAGE = "https://github.com/bastikr/boolean.py"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e319747a5eb94cddf646037c01ddba47"

SRC_URI[md5sum] = "1189d115a38f84f5df743014926a9159"
SRC_URI[sha256sum] = "bd19b412435611ecc712603d0fd7d0e280e24698e7a6e3d5f610473870c5dd1e"

PYPI_PACKAGE = "boolean.py"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
