SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592"

inherit python_hatchling
SRC_URI[sha256sum] = "61c16d2a8576dc0649d9f39e089b5f02bcd27fba10d8fb4dcc28173f7a45151f"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

