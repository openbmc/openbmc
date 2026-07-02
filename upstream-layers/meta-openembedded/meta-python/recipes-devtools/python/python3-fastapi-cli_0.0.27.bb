DESCRIPTION = "Run and manage FastAPI apps from the command line with FastAPI CLI"
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e0cf8d811ea2046529403707b266fb5a"

SRC_URI[sha256sum] = "1dffb1e40c0c88f2e0171a8a252a2b615c1e63ff8c05626649e4badd6a84336a"

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
