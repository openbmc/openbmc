SUMMARY = "This package provides a python interface to CMU Sphinxbase and Pocketsphinx libraries created with SWIG and Setuptools."
DESCRIPTION = "Python interface to CMU Sphinxbase and Pocketsphinx libraries"
HOMEPAGE = "https://github.com/bambocher/pocketsphinx-python"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=826ebda787eb48e78aec2624f9faba72"

SRC_URI[md5sum] = "94d008eebea16acb60a9ffe614575dee"
SRC_URI[sha256sum] = "34d290745c7dbe6fa2cac9815b5c19d10f393e528ecd70e779c21ebc448f9b63"

inherit pypi setuptools3 features_check

DEPENDS += "swig-native pulseaudio"
REQUIRED_DISTRO_FEATURES += "pulseaudio"
