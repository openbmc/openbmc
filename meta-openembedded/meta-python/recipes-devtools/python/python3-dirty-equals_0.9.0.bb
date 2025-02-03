SUMMARY = "Doing dirty (but extremely useful) things with equals."
DESCRIPTION = "dirty-equals is a python library that (mis)uses the \
__eq__ method to make python code (generally unit tests) more \
declarative and therefore easier to read and write.\
\
dirty-equals can be used in whatever context you like, but it comes \
into its own when writing unit tests for applications where you're \
commonly checking the response to API calls and the contents of a database."
HOMEPAGE = "https://github.com/samuelcolvin/dirty-equals"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab599c188b4a314d2856b3a55030c75c"

SRC_URI[sha256sum] = "17f515970b04ed7900b733c95fd8091f4f85e52f1fb5f268757f25c858eb1f7b"

S = "${WORKDIR}/dirty_equals-${PV}"

inherit pypi python_hatchling

PYPI_PACKAGE = "dirty_equals"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
    python3-pytz \
    python3-core \
    python3-json \
    python3-netclient \
"
