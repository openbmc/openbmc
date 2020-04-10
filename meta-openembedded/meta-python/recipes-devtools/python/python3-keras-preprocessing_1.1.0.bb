SUMMARY = "Easy data preprocessing and data augmentation for deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-preprocessing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1744b320500cc2e3112964d00cce7aa4"

SRC_URI = "git://github.com/keras-team/keras-preprocessing.git"
SRCREV ?= "ff90696c0416b74344b91df097b228e694339b88"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

