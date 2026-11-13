SUMMARY = "WSGI HTTP Server for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dc9171ccd8fcbd7827c850148b3ca98"

SRC_URI[sha256sum] = "62b864895d9ebff0b2f9867ba04fe811c93121596540830c9c916d0769668447"

inherit pypi python_setuptools_build_meta ptest

CVE_PRODUCT = "gunicorn"

SRC_URI += " \
	file://run-ptest \
"

# python-misc for wsgiref
RDEPENDS:${PN}-ptest += " \
    bash \
    python3-eventlet \
    python3-gevent \
    python3-misc \
    python3-multiprocessing \
    python3-pytest \
    python3-pytest-asyncio \
    python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	# tests/docker/* are docker-compose/Dockerfile-driven integration tests
	# requiring a Docker daemon, which the ptest QEMU environment doesn't have
	rm -rf ${D}${PTEST_PATH}/tests/docker
	# upstream's pyproject.toml sets asyncio_mode=auto for the async tests
	# under tests/dirty and tests/ctl; provide just that bit without pulling
	# in the rest of upstream's addopts (e.g. --cov=gunicorn)
	cat > ${D}${PTEST_PATH}/pytest.ini <<EOF
[pytest]
asyncio_mode = auto
EOF
}

RDEPENDS:${PN} += "python3-setuptools python3-fcntl"

CVE_STATUS[CVE-2024-1135] = "fixed-version: fixed since v22.0.0"
