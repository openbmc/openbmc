SUMMARY = "Python Library for Tom's Obvious, Minimal Language"
HOMEPAGE = "https://github.com/uiri/toml"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16c77b2b1050d2f03cb9c2ed0edaf4f0"

SRC_URI[sha256sum] = "b3bda1d108d5dd99f4a20d24d9c348e91c4db7ab1b749200bded2f839ccbe68f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
