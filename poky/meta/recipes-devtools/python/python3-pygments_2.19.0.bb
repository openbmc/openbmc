SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592"

inherit python_hatchling
SRC_URI[sha256sum] = "afc4146269910d4bdfabcd27c24923137a74d562a23a320a41a55ad303e19783"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

