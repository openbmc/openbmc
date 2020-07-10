SUMMARY = "Easy data preprocessing and data augmentation for deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-preprocessing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1744b320500cc2e3112964d00cce7aa4"

inherit pypi setuptools3

SRC_URI[md5sum] = "d2a0e10437cfa5c2d09458a84fb58d05"
SRC_URI[sha256sum] = "add82567c50c8bc648c14195bf544a5ce7c1f76761536956c3d2978970179ef3"

PYPI_PACKAGE = "Keras_Preprocessing"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/5e/f1/b44337faca48874333769a29398fe4666686733c8880aa160b9fd5dfe600/Keras_Preprocessing-${PV}.tar.gz"

BBCLASSEXTEND = "native"

