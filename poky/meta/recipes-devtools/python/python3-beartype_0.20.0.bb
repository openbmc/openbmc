SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3a5bc05a109d235912da97a053cd7a58"

SRC_URI[sha256sum] = "599ecc86b88549bcb6d1af626f44d85ffbb9151ace5d7f9f3b493dce2ffee529"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-numbers python3-io"

BBCLASSEXTEND = "native nativesdk"
