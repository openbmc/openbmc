SUMMARY = "Declarative parsing and validation of HTTP request objects, with built-in support for popular web frameworks."
HOMEPAGE = "https://github.com/marshmallow-code/webargs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dfbd4ae0074716275fc229c775723e8f"

inherit pypi setuptools3

SRC_URI[sha256sum] = "ea99368214a4ce613924be99d71db58c269631e95eff4fa09b7354e52dc006a5"

RDEPENDS:${PN} += "\
    python3-marshmallow \
    python3-packaging \
    python3-core \
    python3-json \
    python3-asyncio \
    python3-logging \
"
