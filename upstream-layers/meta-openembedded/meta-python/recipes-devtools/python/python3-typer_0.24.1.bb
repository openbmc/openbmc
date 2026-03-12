SUMMARY = "Typer, build great CLIs. Easy to code. Based on Python type hints."
DESCRIPTION = "\
    Typer is a library for building CLI applications that users will love using and developers will love creating. Based on Python type hints. \
    It's also a command line tool to run scripts, automatically converting them to CLI applications. \
"
HOMEPAGE = "https://github.com/fastapi/typer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=173d405eb704b1499218013178722617"

SRC_URI[sha256sum] = "e39b4732d65fbdcde189ae76cf7cd48aeae72919dea1fdfc16593be016256b45"

inherit pypi python_pdm ptest

SRC_URI:append = " \
    file://run-ptest \
"

PYPI_PACKAGE = "typer"

RDEPENDS:${PN} += "\
    python3-click \
    python3-typing-extensions \
"
# python3-misc for webbrowser module
RDEPENDS:${PN}-ptest += "\
    python3-coverage \
    python3-markdown-it-py \
    python3-misc \
    python3-mypy \
    python3-pluggy \
    python3-pytest \
    python3-pytest-cov \
    python3-pytest-sugar \
    python3-pytest-xdist \
    python3-rich \
    python3-shellingham \
    python3-toml \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
