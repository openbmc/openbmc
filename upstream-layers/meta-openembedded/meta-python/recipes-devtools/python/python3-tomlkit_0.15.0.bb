SUMMARY = "Style preserving TOML library"
HOMEPAGE = "https://pypi.org/project/tomlkit/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82"

SRC_URI[sha256sum] = "7d1a9ecba3086638211b13814ea79c90dd54dd11993564376f3aa92271f5c7a3"

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
