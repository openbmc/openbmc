SUMMARY = "Provides both a high- and low-level interface to the HDF5 library from Python. "
HOMEPAGE = "https://www.h5py.org/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://setup.py;beginline=107;endline=107;md5=795ecad0d261c998cc526c84a822dff6"

SRC_URI = "git://github.com/h5py/h5py.git;branch=master;protocol=https \
           file://0001-cross-compiling-support.patch \
          "
SRCREV ?= "8d96a14c3508de1bde77aec5db302e478dc5dbc4"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

DEPENDS = "python3-pkgconfig-native \
           python3-cython-native \
           python3-numpy-native \
           python3-six-native \
           python3 \
           hdf5 \
          "

RDEPENDS_${PN} = "python3-numpy \
                  python3-six \
                 "

export HDF5_VERSION="1.8.19"
