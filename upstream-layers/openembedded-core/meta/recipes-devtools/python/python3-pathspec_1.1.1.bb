SUMMARY = "Utility library for gitignore style pattern matching of file paths."
HOMEPAGE = "https://github.com/cpburnz/python-path-specification"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI[sha256sum] = "17db5ecd524104a120e173814c90367a96a98d07c45b2e10c2f3919fff91bf5a"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-profile"
