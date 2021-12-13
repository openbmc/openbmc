SUMMARY = "This module provides a monotonic() function which returns the value (in fractional seconds) of a clock which never goes backwards."
HOMEPAGE = "https://github.com/atdt/monotonic"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRCREV = "80681f6604e136e513550342f977edb98f5fc5ad"
SRC_URI = "git://github.com/atdt/monotonic.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit setuptools3
