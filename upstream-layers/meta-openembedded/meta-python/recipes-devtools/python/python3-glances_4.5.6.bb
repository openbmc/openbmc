SUMMARY = "Glances is an open-source system cross-platform monitoring tool."
DESCRIPTION = "Glances an Eye on your system. A top/htop alternative for \
GNU/Linux, BSD, macOS and Windows operating systems."
HOMEPAGE = "https://nicolargo.github.io/glances/"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=852ecadc0ac7e6f4d7144d5544a3815b"

SRC_URI[sha256sum] = "8a26329f0a25e878d53c2558f1eb0615b09acc1dce2ba523cab32dbe175fe8bf"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI += " \
	file://run-ptest \
	git://github.com/nicolargo/glances.git;protocol=https;branch=master;name=testsdata;subpath=tests-data;destsuffix=glances-tests-data \
"
# tests-data/ is used by tests/test_core.py but not shipped in the pypi sdist,
# take it from the v4.5.6 tag
SRCREV_testsdata = "ce2f8506289c71e48ed8bf186f2c04d8256203d5"

# tests/test_core.py runs glances with ./conf/glances.conf and reads plugin
# test data from ./tests-data, both relative to the ptest directory
do_install_ptest:append() {
	install -d ${D}${PTEST_PATH}/conf ${D}${PTEST_PATH}/tests-data
	cp -rf ${S}/conf/* ${D}${PTEST_PATH}/conf/
	cp -rf ${UNPACKDIR}/glances-tests-data/plugins ${D}${PTEST_PATH}/tests-data/
}

# psutil/jinja2/packaging are oe-core; fastapi/uvicorn/defusedxml are
# meta-python.
RDEPENDS:${PN} += " \
    python3-psutil \
    python3-packaging \
    python3-defusedxml \
    python3-fastapi \
    python3-uvicorn \
    python3-jinja2 \
    python3-json \
    python3-logging \
    python3-asyncio \
    python3-multiprocessing \
    python3-netclient \
    python3-xml \
"

RDEPENDS:${PN}-ptest += " \
    python3-requests \
"

# Optional fast-JSON accelerator glances uses if present.
RRECOMMENDS:${PN} += "python3-orjson"
