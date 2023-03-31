SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "3f21d09e1b2aa9f2dacca12da240ca37de3ba5237a93addfd6d593afe9073355"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pytz-deprecation-shim \
        ${PYTHON_PN}-datetime \
"
