
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=95792ff3fe8e11aa49ceb247e66e4810"

SRC_URI[sha256sum] = "96e3702dce09ee0dce48856135620d3d865ca684a79fe7513fd7b13a12f82862"

SRC_URI += "file://run-ptest"

CVE_PRODUCT = "tiangolo:fastapi"

inherit pypi python_pdm ptest-python-pytest

PACKAGECONFIG ?= ""
# swagger-ui is in meta-webserver that meta-python does not depend upon
# Users can still enable it in their distros explicitly but its disabled
# by default
PACKAGECONFIG[swagger-ui] = ",,,swagger-ui"

RDEPENDS:${PN} += "\
    python3-annotated-doc \
    python3-fastapi-cli \
    python3-json \
    python3-pydantic \
    python3-starlette \
    python3-typing-extensions \
"

RDEPENDS:${PN}-ptest += "\
                        python3-coverage \
                        python3-httpx \
                        python3-orjson \
                        python3-dirty-equals \
                        python3-pytest-httpx \
                        python3-python-multipart \
                        python3-sqlalchemy \
                        python3-trio \
"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/scripts
    cp -rf ${S}/scripts/* ${D}${PTEST_PATH}/scripts/
    rm -rf ${D}${PTEST_PATH}/scripts/tests
    echo "import sys; from pathlib import Path; sys.path.insert(0, str(Path(__file__).parent))" > ${D}${PTEST_PATH}/conftest.py
}
