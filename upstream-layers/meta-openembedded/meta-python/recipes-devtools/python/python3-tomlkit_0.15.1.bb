SUMMARY = "Style preserving TOML library"
HOMEPAGE = "https://pypi.org/project/tomlkit/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82"

SRC_URI[sha256sum] = "e25bbf38843005246210a12982776f27f99cb9be67160e14434d0c0d21ee1e97"

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
