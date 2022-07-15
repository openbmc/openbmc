SUMMARY = "Cross-platform colored terminal text."
HOMEPAGE = "https://github.com/tartley/colorama"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b4936429a56a652b84c5c01280dcaa26"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e6c6b4334fc50988a639d9b98aa429a0b57da6e17b9a44f0451f930b6967b7a4"

BBCLASSEXTEND = "native nativesdk"
