SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61b63ea9d36f6fb63ddaaaac8265304f"

SRC_URI[sha256sum] = "b15d3fd043ad0c40b2ff17f7df2ddaec2e880bb923b40d133939a107c97fde5c"

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
"

BBCLASSEXTEND = "native nativesdk"
