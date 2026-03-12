DESCRIPTION = "Run and manage FastAPI apps from the command line with FastAPI CLI"
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e0cf8d811ea2046529403707b266fb5a"

SRC_URI[sha256sum] = "d17c2634f7b96b6b560bc16b0035ed047d523c912011395f49f00a421692bc3a"

inherit pypi python_pdm

PYPI_PACKAGE = "fastapi_cli"

UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += "\
    python3-difflib \
    python3-pydantic \
    python3-rich-toolkit \
    python3-typer \
    python3-uvicorn \
"
