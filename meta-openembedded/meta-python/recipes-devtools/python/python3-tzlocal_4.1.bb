SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "0f28015ac68a5c067210400a9197fc5d36ba9bc3f8eaf1da3cbd59acdfed9e09"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pytz-deprecation-shim \
"
