SUMMARY = "Declarative parsing and validation of HTTP request objects, with built-in support for popular web frameworks."
HOMEPAGE = "https://github.com/marshmallow-code/webargs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_flit_core

CVE_PRODUCT = "webargs"
SRC_URI[sha256sum] = "799bf9039c76c23fd8dc1951107a75a9e561203c15d6ae8f89c1e46e234636c1"

RDEPENDS:${PN} += "\
    python3-marshmallow \
    python3-packaging \
    python3-core \
    python3-json \
    python3-asyncio \
    python3-logging \
"
