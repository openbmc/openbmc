SUMMARY = "Utility library for gitignore style pattern matching of file paths."
HOMEPAGE = "https://github.com/cpburnz/python-path-specification"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI[sha256sum] = "56200de4077d9d0791465aa9095a01d421861e405b5096955051deefd697d6f6"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
