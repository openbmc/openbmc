DESCRIPTION = "FastAPI framework, high performance, easy to learn, fast to code, ready for production"
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=95792ff3fe8e11aa49ceb247e66e4810"

SRC_URI[sha256sum] = "d04115b508d936d254cea545b7312ecaa58a7b3a0f84952535b4c9afae7668cd"

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
