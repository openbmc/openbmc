SUMMARY = "Provides both a high- and low-level interface to the HDF5 library from Python."
HOMEPAGE = "https://www.h5py.org/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=113251d71fb0384712c719b567261c5c"

SRC_URI[sha256sum] = "ede198dde0c359a3f9dc0af15962707c7195102235cb26b4826e33918789559a"

SRC_URI += "file://0001-setup_build.py-avoid-absolute-path.patch"

inherit pkgconfig pypi python_setuptools_build_meta cython

BBCLASSEXTEND = "native"

DEPENDS = "python3-pkgconfig-native \
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
