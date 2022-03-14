SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61b63ea9d36f6fb63ddaaaac8265304f"

SRC_URI[sha256sum] = "0cc514559596fa380bf188e31b0cdf3d9e6d9cc162fdca8a49ed511b291a9ae1"

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
