DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "5c91a66e5e03a4ac5727f20207df1da5681748a154c2bc85ef532fef9e0109d1"


inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-can \
    python3-bitstruct \
    python3-core \
    python3-textparser \
    python3-typing-extensions \
    python3-diskcache \
    python3-asyncio \
"
