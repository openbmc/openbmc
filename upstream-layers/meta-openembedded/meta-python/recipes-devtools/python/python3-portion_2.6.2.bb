DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000208d539ec061b899bce1d9ce9404"

inherit pypi python_hatchling ptest-python-pytest

SRC_URI[sha256sum] = "fbf334143dbac5d07ffa411784e2b29e4e1f21203385019a93e2c1a8f443da16"

RDEPENDS:${PN} += "\
	python3-sortedcontainers \
"

RDEPENDS:${PN}-ptest += "\
	python3-pytest-benchmark \
"

do_install_ptest:append () {
	# test_doc.py tests README.md.
	install -Dm 0644 ${S}/README.md ${D}${PTEST_PATH}/README.md
}

BBCLASSEXTEND = "native"
