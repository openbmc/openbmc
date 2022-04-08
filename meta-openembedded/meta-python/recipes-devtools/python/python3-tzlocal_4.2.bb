SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "ee5842fa3a795f023514ac2d801c4a81d1743bbe642e3940143326b3a00addd7"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pytz-deprecation-shim \
        ${PYTHON_PN}-datetime \
"
