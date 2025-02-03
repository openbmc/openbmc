SUMMARY = "Easy data preprocessing and data augmentation for deep learning models"
HOMEPAGE = "https://github.com/keras-team/keras-preprocessing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1744b320500cc2e3112964d00cce7aa4"

inherit pypi setuptools3

SRC_URI[sha256sum] = "add82567c50c8bc648c14195bf544a5ce7c1f76761536956c3d2978970179ef3"

PYPI_PACKAGE = "Keras_Preprocessing"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native"

