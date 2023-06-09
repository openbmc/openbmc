SUMMARY = "Generate JSON-RPC requests and parse responses in Python"
HOMEPAGE = "https://github.com/explodinglabs/jsonrpcclient"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=10f3d7679914df805c98fb351172e677"

SRC_URI[sha256sum] = "c0d475494b3e1b591ecdee7883739accaf5695edb673f16b7383b8c6bbdb1ca3"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-json \
    python3-math \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
