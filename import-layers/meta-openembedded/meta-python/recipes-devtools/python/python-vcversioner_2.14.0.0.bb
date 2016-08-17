SUMMARY = "Python vcversioner, automagically update the project's version"
HOMEPAGE = "https://github.com/habnabit/vcversioner"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=260625d695c5e0c9dd2c2ef898833c7d"

SRC_URI[md5sum] = "7848a365ced9941053bc25d9a9f8f4b4"
SRC_URI[sha256sum] = "acd43686e92e6c8bbeb4f2eef54408567a7adea9692fa72d591eec5357c03b86"

inherit pypi setuptools

do_compile_append() {
    export BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS}
    ${PYTHON} setup.py -q bdist_egg --dist-dir ./
}

do_install_append() {
    install -m 0644 ${S}/vcversioner*.egg ${D}/${PYTHON_SITEPACKAGES_DIR}/
}

BBCLASSEXTEND = "native"
