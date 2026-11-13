SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2597b30908b2171ba6cb2080fd5be4c5"

SRC_URI[sha256sum] = "8b805f246b32931c74f2b321ea0939fe5bb2ff43c12a3eff19cc4c854b832e1d"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-numbers python3-io"

BBCLASSEXTEND = "native nativesdk"
