SUMMARY = "JavaScript unobfuscator and beautifier."
HOMEPAGE = "https://beautifier.io/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3 ptest

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "9579d4e9dbaa00383f3efdff4c98c8140bb85ba319398e8b97cdaba27abd6ba3"

RDEPENDS:${PN} += "\
    python3-core \
    python3-stringold \
    python3-shell \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/${PYPI_PACKAGE}/tests/* ${D}${PTEST_PATH}/tests/

    cat > ${D}${PTEST_PATH}/pytest.ini <<EOF
[pytest]
python_files = test*.py
EOF
}

BBCLASSEXTEND = "native nativesdk"
