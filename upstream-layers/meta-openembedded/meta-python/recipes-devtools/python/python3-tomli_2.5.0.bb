SUMMARY = "A lil' TOML parser"
DESCRIPTION = "Tomli is a Python library for parsing TOML. Tomli is fully \
compatible with TOML v1.0.0."
HOMEPAGE = "https://github.com/hukkin/tomli"
BUGTRACKER = "https://github.com/hukkin/tomli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "264507556cd8b8c8e7c6ee037cdf443a463f03f4c958e57195e3d369711b8ff6"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-stringold \
"
