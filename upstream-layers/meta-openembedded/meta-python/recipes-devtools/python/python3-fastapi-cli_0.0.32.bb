DESCRIPTION = "Run and manage FastAPI apps from the command line with FastAPI CLI"
HOMEPAGE = "https://fastapi.tiangolo.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e0cf8d811ea2046529403707b266fb5a"

SRC_URI[sha256sum] = "38024d2345275e1b37ce8848727a580d84901b570e96b3256d9d36a9a5039424"

inherit pypi python_pdm

RDEPENDS:${PN} += "\
    python3-difflib \
    python3-pydantic \
    python3-rich-toolkit \
    python3-typer \
    python3-uvicorn \
"
