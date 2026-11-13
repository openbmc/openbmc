SUMMARY = "Style preserving TOML library"
HOMEPAGE = "https://pypi.org/project/tomlkit/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82"

SRC_URI[sha256sum] = "83e32755ba1d20d9340bd069ee97e9f6232ba2346b63ffcba425de01f7e327a6"

inherit pypi python_poetry_core ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-profile \
    python3-stringold \
"

RDEPENDS:${PN}-ptest += " \
    python3-poetry-core \
    python3-pyyaml \
"

BBCLASSEXTEND = "native nativesdk"
