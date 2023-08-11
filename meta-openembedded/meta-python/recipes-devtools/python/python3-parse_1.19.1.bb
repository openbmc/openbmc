SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ab458ad281b60e6f1b39b3feafbfc05"

SRC_URI[sha256sum] = "cc3a47236ff05da377617ddefa867b7ba983819c664e1afe46249e5b469be464"

SRC_URI += " \
    git://github.com/r1chardj0n3s/parse.git;branch=master;protocol=https \
    file://run-ptest \
"

SRCREV ?= "72776522285d516032faa0f80c4ee6a8964075e8"

S = "${WORKDIR}/git"

inherit python_setuptools_build_meta ptest

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-logging \
    python3-numbers \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
"

do_install_ptest() {
    cp -f ${S}/test_parse.py ${D}${PTEST_PATH}/
}
