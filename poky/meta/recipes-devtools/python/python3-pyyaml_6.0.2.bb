SUMMARY = "Python support for YAML"
DEPENDS += "libyaml python3-cython-native"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "d584d9ec91ad65861cc08d42e834324ef890a082e591037abe114850ff7bbc3e"
UPSTREAM_CHECK_PYPI_PACKAGE = "PyYAML"

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
	cp -rf ${UNPACKDIR}/test_dump_load.py ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
