DESCRIPTION = "Run and manage FastAPI apps from the command line with FastAPI CLI"
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e0cf8d811ea2046529403707b266fb5a"

SRC_URI[sha256sum] = "1afc9c9e21d7ebc8a3ca5e31790cd8d837742be7e4f8b9236e99cb3451f0de00"

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
