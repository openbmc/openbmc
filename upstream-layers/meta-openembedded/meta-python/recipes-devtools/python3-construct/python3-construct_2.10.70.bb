SUMMARY = "A powerful declarative symmetric parser/builder for binary data"
HOMEPAGE = "http://construct.readthedocs.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=202b39559c1c79fe4715ce81e9e0ac02"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

SRC_URI = "\
    git://github.com/construct/construct.git;protocol=https;branch=master;tag=v${PV} \
    file://0001-add-pytest.ini-file-to-enable-pytest-8.x.y-to-find-t.patch \
    file://0002-pytest.ini-disable-the-benchmark.patch \
"

SRCREV = "c25a47172d4bde392b7ad188175b07b319d3dea4"

inherit setuptools3 ptest-python-pytest

PACKAGECONFIG ?= "extras"
PACKAGECONFIG[extras] = ",,,python3-arrow python3-cloudpickle python3-cryptography python3-lz4 python3-numpy python3-ruamel-yaml"

RDEPENDS:${PN} += "\
    python3-compression \
    python3-core \
    python3-crypt \
    python3-cryptography \
    python3-debugger \
    python3-misc \
    python3-pickle \
"

RDEPENDS:${PN}-ptest += "python3-pytest-benchmark"

do_install_ptest:append() {
    # *.so files gives issues with QA of Yocto/OpenEmbedded
    rm -rf ${D}${PTEST_PATH}/tests/deprecated_gallery
    rm -rf ${D}${PTEST_PATH}/tests/gallery

    install -d ${D}${PTEST_PATH}
    install -m 0644 ${S}/pytest.ini ${D}${PTEST_PATH}
}
