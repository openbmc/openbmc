SUMMARY = "Read key-value pairs from a .env file and set them as environment variables"
HOMEPAGE = "https://github.com/theskumar/python-dotenv"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e914cdb773ae44a732b392532d88f072"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

SRC_URI[sha256sum] = "42667e897e16ab0d66954af0e60a9caa94f0fd4ecf3aaf6d2d260eec1aa36ad6"

SRC_URI += "file://0001-test_main.py-skip-two-test-when-running-as-root.patch"

inherit pypi python_setuptools_build_meta ptest-python-pytest

# Uncomment this line to enable all the optional features.
PACKAGECONFIG ?= ""
PACKAGECONFIG:append = "${@bb.utils.contains('PTEST_ENABLED', '1', ' cli', '', d)}"
PACKAGECONFIG[cli] = ",,,python3-click"

PYPI_PACKAGE = "python_dotenv"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN}-ptest += "\
    coreutils \
    python3-sh \
"
