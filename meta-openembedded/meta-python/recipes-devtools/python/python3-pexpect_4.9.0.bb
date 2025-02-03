SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRC_URI += "file://0001-FSM.py-change-shebang-from-python-to-python3.patch"

SRC_URI[sha256sum] = "ee7d41123f3c9911050ea2c2dac107568dc43b2d3b0c7557a33212c398ead30f"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-core \
    python3-io \
    python3-terminal \
    python3-resource \
    python3-fcntl \
    python3-ptyprocess \
"

BBCLASSEXTEND = "native nativesdk"
