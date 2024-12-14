SUMMARY = "Declarative parsing and validation of HTTP request objects, with built-in support for popular web frameworks."
HOMEPAGE = "https://github.com/marshmallow-code/webargs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "b8d098ab92bd74c659eca705afa31d681475f218cb15c1e57271fa2103c0547a"

RDEPENDS:${PN} += "\
    python3-marshmallow \
    python3-packaging \
    python3-core \
    python3-json \
    python3-asyncio \
    python3-logging \
"
