DESCRIPTION = "GenSON is a powerful, user-friendly JSON Schema generator built in Python."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6c30d55dbfb3a28d420d918534adf6b"

# The PyPI package omits files for testing
# so use the GitHub source instead.
SRCREV = "f305b73f08998891d2f7804b84c8e3c29ca96209"
SRC_URI = " \
    git://github.com/wolverdude/GenSON;branch=master;protocol=https \
    file://run-ptest \
"

inherit python_setuptools_build_meta ptest

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-coverage \
    python3-jsonschema \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
