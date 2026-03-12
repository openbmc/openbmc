SUMMARY = "Better dates and times for Python"
HOMEPAGE = "https://github.com/arrow-py/arrow"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=14a2e29a9d542fb9052d75344d67619d"

SRC_URI:append = " \
    file://run-ptest \
"
SRC_URI[sha256sum] = "ed0cc050e98001b8779e84d461b0098c4ac597e88704a655582b21d116e526d7"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
    python3-dateparser \
    python3-dateutil-zoneinfo \
    python3-pre-commit \
    python3-pytest-cov \
    python3-pytest-mock \
    python3-pytz \
    python3-simplejson \
    python3-types-python-dateutil \
"

RDEPENDS:${PN} += " \
    python3-dateutil \
    python3-tzdata \
"
