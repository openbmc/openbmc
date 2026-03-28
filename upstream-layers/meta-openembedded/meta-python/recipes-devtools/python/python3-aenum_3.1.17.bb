SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "a969a4516b194895de72c875ece355f17c0d272146f7fda346ef74f93cf4d5ba"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	python3-pprint \
"

BBCLASSEXTEND = "native nativesdk"
