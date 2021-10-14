SUMMARY = "Provides both a high- and low-level interface to the HDF5 library from Python."
HOMEPAGE = "https://www.h5py.org/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=113251d71fb0384712c719b567261c5c"

SRC_URI[sha256sum] = "ee1c683d91ab010d5e85cb61e8f9e7ee0d8eab545bf3dd50a9618f1d0e8f615e"

SRC_URI:append = " \
           file://0001-setup_build.py-avoid-absolute-path.patch \
           file://0001-setup.py-Fix-numpy-version.patch \
          "

inherit pkgconfig pypi setuptools3

BBCLASSEXTEND = "native"

DEPENDS = "python3-pkgconfig-native \
           python3-cython-native \
           python3-numpy-native \
           python3-six-native \
           hdf5-native \
           python3 \
           hdf5 \
          "

RDEPENDS:${PN} = "python3-numpy \
                  python3-six \
                  python3-json \
                 "

export HDF5_VERSION="1.8.21"
