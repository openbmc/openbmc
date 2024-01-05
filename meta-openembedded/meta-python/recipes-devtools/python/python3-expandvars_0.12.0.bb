SUMMARY = "Expand system variables Unix style"
HOMEPAGE = "https://github.com/sayanarijit/expandvars"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b2e744064bd184728ac09dbfb52aaf4"

SRC_URI[sha256sum] = "7d1adfa55728cf4b5d812ece3d087703faea953e0c0a1a78415de9df5024d844"

inherit pypi python_hatchling python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
