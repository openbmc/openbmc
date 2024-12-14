SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

inherit pypi ptest python_setuptools_build_meta

SRC_URI[sha256sum] = "35275439b574837a6cd3020a5a4a73645eb125ce4152a73a2f126bf164b91b80"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
	python3-setuptools \
	python3-packaging \
	python3-platformdirs \
"
# python3-misc for timeit.py 
RDEPENDS:${PN}-ptest += " \
	python3-appdirs \
	python3-attrs \
	python3-flexcache \
	python3-flexparser \
	python3-misc \
	python3-pytest \
	python3-pytest-benchmark \
	python3-pytest-subtests \
	python3-statistics \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/pint/* ${D}${PTEST_PATH}/
}
