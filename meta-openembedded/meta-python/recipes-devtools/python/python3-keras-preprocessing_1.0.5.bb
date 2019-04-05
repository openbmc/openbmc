SUMMARY = "Easy data preprocessing and data augmentation for deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-preprocessing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1744b320500cc2e3112964d00cce7aa4"

SRC_URI = "git://github.com/keras-team/keras-preprocessing.git"
SRCREV ?= "362fe9f8daf556151328eb5d02bd5ae638c653b8"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

