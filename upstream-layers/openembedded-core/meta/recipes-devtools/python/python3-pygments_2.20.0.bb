SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592"

inherit python_hatchling
SRC_URI[sha256sum] = "6757cd03768053ff99f3039c1a36d6c0aa0b263438fcab17520b30a303a82b5f"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

