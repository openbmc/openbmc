SUMMARY = "Utility library for gitignore style pattern matching of file paths."
HOMEPAGE = "https://github.com/cpburnz/python-path-specification"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI[sha256sum] = "0210e2ae8a21a9137c0d470578cb0e595af87edaa6ebf12ff176f14a02e0e645"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-profile"
