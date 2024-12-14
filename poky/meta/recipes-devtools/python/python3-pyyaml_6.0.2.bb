SUMMARY = "Python support for YAML"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

SRC_URI[sha256sum] = "d584d9ec91ad65861cc08d42e834324ef890a082e591037abe114850ff7bbc3e"

SRC_URI += "\
    https://raw.githubusercontent.com/yaml/pyyaml/a98fd6088e81d7aca571220c966bbfe2ac43c335/tests/test_dump_load.py;name=test \
    file://run-ptest \
"
SRC_URI[test.sha256sum] = "b6a8a2825d89fdc8aee226560f66b8196e872012a0ea7118cbef1a832359434a"

UPSTREAM_CHECK_PYPI_PACKAGE = "PyYAML"

inherit pypi python_setuptools_build_meta ptest cython

PACKAGECONFIG ?= "libyaml"
PACKAGECONFIG[libyaml] = "--with-libyaml,--without-libyaml,libyaml"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${UNPACKDIR}/test_dump_load.py ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-netclient \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

BBCLASSEXTEND = "native nativesdk"
