SUMMARY = "Declarative parsing and validation of HTTP request objects, with built-in support for popular web frameworks."
HOMEPAGE = "https://github.com/marshmallow-code/webargs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "0c617dec19ed4f1ff6b247cd73855e949d87052d71900938b71f0cafd92f191b"

RDEPENDS:${PN} += "\
    python3-marshmallow \
    python3-packaging \
    python3-core \
    python3-json \
    python3-asyncio \
    python3-logging \
"
