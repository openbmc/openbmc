DESCRIPTION = "A portable, lightweight MessagePack serializer and deserializer written in pure Python."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9eb4691bcb66360acee473a8cf75f594"

SRC_URI[md5sum] = "231609d87aa58459a1491c8f5df4e4cd"
SRC_URI[sha256sum] = "996e4c4454771f0ff0fd2a7566b1a159d305d3611cd755addf444e3533e2bc54"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
"

do_install_ptest() {
       cp -f ${S}/test_umsgpack.py ${D}${PTEST_PATH}/
}

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
