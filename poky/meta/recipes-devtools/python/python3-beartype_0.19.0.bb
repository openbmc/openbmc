SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e71f94261c1b39896cacacfeaf60560e"

SRC_URI[sha256sum] = "de42dfc1ba5c3710fde6c3002e3bd2cad236ed4d2aabe876345ab0b4234a6573"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-numbers python3-io"

BBCLASSEXTEND = "native nativesdk"
