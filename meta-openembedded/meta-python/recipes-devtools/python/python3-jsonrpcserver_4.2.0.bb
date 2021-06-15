SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

SRC_URI[sha256sum] = "0c9e5b9445621138521e912016ae39b3badadd2607140dcbb0c8062934ab4854"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
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

