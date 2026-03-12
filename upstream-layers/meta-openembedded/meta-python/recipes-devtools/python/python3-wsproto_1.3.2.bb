SUMMARY = "WebSockets state-machine based protocol implementation"
HOMEPAGE = "https://github.com/python-hyper/wsproto/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69fabf732409f4ac61875827b258caaf"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "b86885dcf294e15204919950f666e06ffc6c7c114ca900b060d6e16293528294"

RDEPENDS:${PN} += " \
        python3-h11 \
        python3-netclient \
"
