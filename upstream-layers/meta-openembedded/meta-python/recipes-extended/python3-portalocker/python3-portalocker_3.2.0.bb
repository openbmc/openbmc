SUMMARY = "Cross-platform locking library"
DESCRIPTION = "Portalocker is a library to provide an easy API to file locking"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=152634da660a374ca18c0734ed07c63c"

SRC_URI[sha256sum] = "1f3002956a54a8c3730586c5c77bf18fae4149e07eaf1c29fc3faf4d5a3f89ac"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-multiprocessing \
	python3-pytest \
	python3-redis \
	python3-unittest-automake-output \
	redis \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/portalocker_tests
	cp -rf ${S}/portalocker_tests/* ${D}${PTEST_PATH}/portalocker_tests/
	rm -rf ${D}${PTEST_PATH}/portalocker_tests/test_combined.py
}

RDEPENDS:${PN} += " \
	python3-fcntl \
	python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
