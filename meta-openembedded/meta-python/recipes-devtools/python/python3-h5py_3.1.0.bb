SUMMARY = "Provides both a high- and low-level interface to the HDF5 library from Python."
HOMEPAGE = "https://www.h5py.org/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=113251d71fb0384712c719b567261c5c"

SRC_URI[sha256sum] = "1e2516f190652beedcb8c7acfa1c6fa92d99b42331cbef5e5c7ec2d65b0fc3c2"

SRC_URI_append = " \
           file://0001-setup_build.py-avoid-absolute-path.patch \
           file://0001-setup.py-Fix-numpy-version.patch \
          "

inherit pypi setuptools3

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

export HDF5_VERSION="1.8.21"
