SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "bfaf9589bdb418ee3a986d85750c7318d9d2839c1b1a1d6fe8fc53ec201cf140"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	python3-pprint \
"

BBCLASSEXTEND = "native nativesdk"
