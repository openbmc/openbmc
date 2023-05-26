DESCRIPTION = "A portable, lightweight MessagePack serializer and deserializer written in pure Python."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17df11353545c53a2df0ce7655859306"

SRC_URI[sha256sum] = "b801a83d6ed75e6df41e44518b4f2a9c221dc2da4bcd5380e3a0feda520bc61a"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
"

do_install_ptest() {
       cp -f ${S}/test_umsgpack.py ${D}${PTEST_PATH}/
}

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
