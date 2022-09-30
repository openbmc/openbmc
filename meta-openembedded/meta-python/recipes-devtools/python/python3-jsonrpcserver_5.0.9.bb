SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/explodinglabs/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61b63ea9d36f6fb63ddaaaac8265304f"

SRC_URI[sha256sum] = "a71fb2cfa18541c80935f60987f92755d94d74141248c7438847b96eee5c4482"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-apply-defaults \
    python3-asyncio \
    python3-core \
    python3-json \
    python3-jsonschema \
    python3-logging \
    python3-netclient \
    python3-pkgutil \
    python3-oslash \
    python3-typing-extensions \
"

BBCLASSEXTEND = "native nativesdk"
