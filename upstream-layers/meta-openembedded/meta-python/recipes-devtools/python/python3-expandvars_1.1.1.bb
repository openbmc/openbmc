SUMMARY = "Expand system variables Unix style"
HOMEPAGE = "https://github.com/sayanarijit/expandvars"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b2e744064bd184728ac09dbfb52aaf4"

SRC_URI[sha256sum] = "98add8268b760dfee457bde1c17bf745795fdebc22b7ddab75fd3278653f1e05"

inherit pypi python_hatchling python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
