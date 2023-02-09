SUMMARY = "Utility library for gitignore style pattern matching of file paths."
HOMEPAGE = "https://github.com/cpburnz/python-path-specification"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI[sha256sum] = "64d338d4e0914e91c1792321e6907b5a593f1ab1851de7fc269557a21b30ebbc"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
