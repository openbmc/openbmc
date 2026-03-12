SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3a5bc05a109d235912da97a053cd7a58"

SRC_URI[sha256sum] = "8f82b54aa723a2848a56008d18875f91c1db02c32ef6a62319a002e3e25a975f"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-numbers python3-io"

BBCLASSEXTEND = "native nativesdk"
