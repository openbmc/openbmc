SUMMARY = "Rich toolkit for building command-line applications"
HOMEPAGE = "https://github.com/patrick91/rich-toolkit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://pyproject.toml;md5=29c91c89ee62891477a1476375143bef;beginline=6;endline=6"

SRC_URI[sha256sum] = "223dd2cfba325ed55e94933b9e53f3aca13e9fdf76622bd564c18109a2273c1b"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "rich_toolkit"

RDEPENDS:${PN} = "\
    python3-click \
    python3-rich \
    python3-typing-extensions \
    python3-inline-snapshot \
"

RDEPENDS:${PN}-ptest += "\
    python3-pydantic \
"
