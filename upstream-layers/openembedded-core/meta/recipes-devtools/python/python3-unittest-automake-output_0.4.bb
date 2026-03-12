SUMMARY = "Modules to make unittest and pytest look like Automake output, for ptest"
HOMEPAGE = "https://gitlab.com/rossburton/python-unittest-automake-output"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f6f16008d9fb7349f06609329f1ab93b"

SRC_URI = "git://gitlab.com/rossburton/python-unittest-automake-output;protocol=https;branch=main;tag=${PV}"

SRCREV = "4d64324a8910ad28b3157f902c35f19fcb479bc0"

inherit python_flit_core

RDEPENDS:${PN} += "python3-unittest"

BBCLASSEXTEND = "native nativesdk"
