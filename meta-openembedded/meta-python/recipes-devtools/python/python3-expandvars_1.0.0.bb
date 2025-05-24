SUMMARY = "Expand system variables Unix style"
HOMEPAGE = "https://github.com/sayanarijit/expandvars"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b2e744064bd184728ac09dbfb52aaf4"

SRC_URI[sha256sum] = "f04070b8260264185f81142cd85e5df9ceef7229e836c5844302c4ccfa00c30d"

inherit pypi python_hatchling python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
