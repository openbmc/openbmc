SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi python3-pycparser"

SRC_URI[sha256sum] = "f3157624b7558b914cb039fd1af735e5e8049a87c817cc215109ad1c8779df76"

SRC_URI += "file://run-ptest"

inherit pypi python_setuptools_build_meta ptest

do_install_ptest() {
    cp -r ${S}/testing ${D}${PTEST_PATH}
    # These two files from the source tree are needed by the tests
    install -D -m644 ${S}/src/c/parse_c_type.c ${D}${PTEST_PATH}/src/c/parse_c_type.c
    install -D -m644 ${S}/src/cffi/parse_c_type.h ${D}${PTEST_PATH}/src/cffi/parse_c_type.h
}

RDEPENDS:${PN} = " \
    python3-ctypes \
    python3-io \
    python3-pycparser \
    python3-setuptools \
    python3-shell \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-dev \
    gcc-symlinks \
    g++-symlinks \
"

INSANE_SKIP:${PN}-ptest = "dev-deps"

BBCLASSEXTEND = "native nativesdk"
