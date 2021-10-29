SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42976c55ba05d15b32a7b4757dee5e64"

SRC_URI[sha256sum] = "c7a880e229df96759f955d4f3970d4cabce79f60f5b18830c08b90ce77cd5fdc"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
