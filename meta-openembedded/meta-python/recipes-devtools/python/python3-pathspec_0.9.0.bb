SUMMARY = "Utility library for gitignore style pattern matching of file paths."
HOMEPAGE = "https://github.com/cpburnz/python-path-specification"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI[sha256sum] = "e564499435a2673d586f6b2130bb5b95f04a3ba06f81b8f895b651a3c76aabb1"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
