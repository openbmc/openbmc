SUMMARY = "Multi volume file wrapper library"
HOMEPAGE = "https://codeberg.org/miurahr/multivolume"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "a0648d0aafbc96e59198d5c17e9acad7eb531abea51035d08ce8060dcad709d6"

inherit python_setuptools_build_meta pypi

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-io \
    python3-core \
    python3-mmap \
"
