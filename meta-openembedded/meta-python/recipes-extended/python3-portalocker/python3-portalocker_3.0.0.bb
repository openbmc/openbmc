SUMMARY = "Cross-platform locking library"
DESCRIPTION = "Portalocker is a library to provide an easy API to file locking"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=152634da660a374ca18c0734ed07c63c"

SRC_URI[sha256sum] = "21f535de2e7a82c94c130c054adb5c7421d480d5619d61073996e2f89bcb879b"

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
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/portalocker_tests/* ${D}${PTEST_PATH}/tests/
	rm -rf ${D}${PTEST_PATH}/tests/test_combined.py
}

RDEPENDS:${PN} += " \
	python3-fcntl \
	python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
