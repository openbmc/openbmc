SUMMARY = "Reference implementations of popular deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-applications"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=366e2fd3c9714f162d3663b6f97cfe41"

SRC_URI = "git://github.com/keras-team/keras-applications.git"
SRCREV ?= "61de21815728aa66377ebb2a7b4c2f5492a34dd2"


inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} = "python3-numpy \
                  python3-h5py \
                 "
