SUMMARY = "Allows you to use fixtures in @pytest.mark.parametrize."
DESCRIPTION = "Use your fixtures in @pytest.mark.parametrize. \
\
This project was inspired by pytest-lazy-fixture.\
\
Improvements that have been made in this project:\
\
* You can use fixtures in any data structures\
* You can access the attributes of fixtures\
* You can use functions in fixtures"
HOMEPAGE = "https://github.com/dev-petrov/pytest-lazy-fixtures"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4475e66fcfabe27395e6764c8f69c876"

SRC_URI[sha256sum] = "c494b52d798890033d64b28687a4d52807c8b0f606d56316e139df0cbe116c57"

inherit pypi python_poetry_core

PYPI_PACKAGE = "pytest_lazy_fixtures"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-pytest"
