SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

SRC_URI[md5sum] = "d41e9f6b310cb29b0d8f213ff9d57567"
SRC_URI[sha256sum] = "240c517f49b0fdd3bfa428c9a7cc581126a0c43eca60d29762da124017d9d9f4"

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
    python3-typing \
"

BBCLASSEXTEND = "native nativesdk"