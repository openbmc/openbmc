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
LIC_FILES_CHKSUM = "file://LICENSE;md5=aa97bb3778992892e226b4504b83b60c"

SRC_URI[sha256sum] = "f4ac74ee88f2d11e2fa0f65eb30ee4f07105c5f86f4dc92b09eb1138775027c3"

S = "${UNPACKDIR}/dirty_equals-${PV}"

inherit pypi python_hatchling

PYPI_PACKAGE = "dirty_equals"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
    python3-pytz \
    python3-core \
    python3-json \
    python3-netclient \
"
