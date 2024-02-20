SUMMARY = "Python support for YAML"
DEPENDS += "libyaml python3-cython-native"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

PYPI_PACKAGE = "PyYAML"

inherit pypi python_setuptools_build_meta

SRC_URI += "file://0001-Fix-builds-with-Cython-3.patch"
SRC_URI[sha256sum] = "bfdf460b1736c775f2ba9f6a92bca30bc2095067b8a9d77876d1fad6cc3b4a43"

PACKAGECONFIG ?= "libyaml"
PACKAGECONFIG[libyaml] = "--with-libyaml,--without-libyaml,libyaml"

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-netclient \
"

inherit ptest
SRC_URI += "\
    https://raw.githubusercontent.com/yaml/pyyaml/a98fd6088e81d7aca571220c966bbfe2ac43c335/tests/test_dump_load.py;name=test \
    file://run-ptest \
"
SRC_URI[test.sha256sum] = "b6a8a2825d89fdc8aee226560f66b8196e872012a0ea7118cbef1a832359434a"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${WORKDIR}/test_dump_load.py ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
