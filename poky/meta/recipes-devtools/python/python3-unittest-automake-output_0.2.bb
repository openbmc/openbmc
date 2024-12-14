SUMMARY = "Modules to make unittest and pytest look like Automake output, for ptest"
HOMEPAGE = "https://gitlab.com/rossburton/python-unittest-automake-output"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f6f16008d9fb7349f06609329f1ab93b"

SRC_URI = "git://gitlab.com/rossburton/python-unittest-automake-output;protocol=https;branch=main"
SRCREV = "aebdfb188e368c690ea55cf6c9c9ffa1a52def65"

S = "${WORKDIR}/git"

inherit python_flit_core

RDEPENDS:${PN} += "python3-unittest"

BBCLASSEXTEND = "native nativesdk"
