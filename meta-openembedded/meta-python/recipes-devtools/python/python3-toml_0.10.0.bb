SUMMARY = "Python Library for Tom's Obvious, Minimal Language"
HOMEPAGE = "https://github.com/uiri/toml"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d6012eea477117abf51c31262a152f8"

SRC_URI[md5sum] = "63fffbe2d632865ec29cd69bfdf36682"
SRC_URI[sha256sum] = "229f81c57791a41d65e399fc06bf0848bab550a9dfd5ed66df18ce5f05e73d5c"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
