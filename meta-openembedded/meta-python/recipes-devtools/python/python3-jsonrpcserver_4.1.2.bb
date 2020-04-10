SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

SRC_URI[md5sum] = "fd4091bc19eb18579c15b97af70714eb"
SRC_URI[sha256sum] = "73db55d1cf245ebdfb96ca05c4cce01c51b61be845a2a981f539ea1e6a4e0c4a"

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

