SUMMARY = "Reference implementations of popular deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-applications"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=366e2fd3c9714f162d3663b6f97cfe41"

SRC_URI = "git://github.com/keras-team/keras-applications.git;branch=master;protocol=https"
SRCREV ?= "3b180cb10eda683dda7913ecee2e6487288d292d"


inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} = "python3-numpy \
                  python3-h5py \
                 "
