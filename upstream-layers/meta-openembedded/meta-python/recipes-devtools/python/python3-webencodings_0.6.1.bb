SUMMARY = "Character encoding aliases for legacy web content"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78ecd9e2dd613fe63d51efca9b793f2c"

SRC_URI[sha256sum] = "565f9ad031c702dae404e27a099e3e09186a3ab1b9520f06d215502b651fd910"

inherit pypi python_flit_core

RDEPENDS:${PN} += " \
    python3-codecs \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
