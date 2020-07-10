SUMMARY = "Library to process JSON-RPC requests"
HOMEPAGE = "https://github.com/bcb/jsonrpcserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

SRC_URI[md5sum] = "e73b0bd90e99115d3f9e0ac42882c5b7"
SRC_URI[sha256sum] = "649680c293facb6ae7c3f5c8028e4623c55195db5216847e9f25f85cba2d443a"

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

