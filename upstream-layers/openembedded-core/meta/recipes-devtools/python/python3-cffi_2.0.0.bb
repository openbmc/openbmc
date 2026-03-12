SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT-0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0158ab9b75875f3bb7fea081d388818"
DEPENDS += "libffi python3-pycparser"

SRC_URI[sha256sum] = "44d1b5909021139fe36001ae048dbdde8214afa20200eda0f64c068cac5d5529"

SRC_URI += "file://run-ptest \
            file://0001-Make-test_parsing-more-resilient-to-changes-in.patch"

inherit pypi python_setuptools_build_meta ptest pkgconfig

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
