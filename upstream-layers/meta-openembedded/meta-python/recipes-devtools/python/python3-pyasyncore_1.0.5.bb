SUMMARY = "Make asyncore available for Python 3.12 onwards"
HOMEPAGE = "https://github.com/simonrob/pyasyncore"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5605fc335ce1bab614032468d0a1e00"

inherit pypi setuptools3 ptest
SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "dd483d5103a6d59b66b86e0ca2334ad43dca732ff23a0ac5d63c88c52510542e"

RDEPENDS:${PN} += "python3-core python3-io"
RDEPENDS:${PN}-ptest += "python3-tests"

do_install_ptest:append(){
    cp -r ${S}/tests ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native nativesdk"
