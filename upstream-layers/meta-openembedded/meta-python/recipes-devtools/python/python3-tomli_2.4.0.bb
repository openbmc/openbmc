SUMMARY = "A lil' TOML parser"
DESCRIPTION = "Tomli is a Python library for parsing TOML. Tomli is fully \
compatible with TOML v1.0.0."
HOMEPAGE = "https://github.com/hukkin/tomli"
BUGTRACKER = "https://github.com/hukkin/tomli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "aa89c3f6c277dd275d8e243ad24f3b5e701491a860d5121f2cdd399fbb31fc9c"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-stringold \
"
