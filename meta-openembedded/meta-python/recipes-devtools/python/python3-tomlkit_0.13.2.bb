SUMMARY = "Style preserving TOML library"
HOMEPAGE = "https://pypi.org/project/tomlkit/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82"

SRC_URI[sha256sum] = "fff5fe59a87295b278abd31bec92c15d9bc4a06885ab12bcea52c71119392e79"

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
