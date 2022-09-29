SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/explodinglabs/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61b63ea9d36f6fb63ddaaaac8265304f"

SRC_URI[sha256sum] = "5150071e4abc9a93f086aa0fd0004dfe0410de66adfaaf513613baa2c2fc00d7"

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
