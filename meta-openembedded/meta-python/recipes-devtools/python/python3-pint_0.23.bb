SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit ptest python_setuptools_build_meta

SRCREV = "f2e4081aee38f850938048beac7fb69c4908bc5e"
SRC_URI = "git://github.com/hgrecco/pint;protocol=https;branch=master"
S = "${WORKDIR}/git"
#SRC_URI[sha256sum] = "e1509b91606dbc52527c600a4ef74ffac12fff70688aff20e9072409346ec9b4"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
    python3-setuptools \
    python3-packaging \
"

RDEPENDS:${PN}-ptest += " \
	python3-appdirs \
	python3-flexcache \
	python3-flexparser \
	python3-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/pint/* ${D}${PTEST_PATH}/
}
