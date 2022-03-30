SUMMARY = "Python bindings for Fast Artificial Neural Networks 2.2.0 (FANN >= 2.2.0)"
SECTION = "devel/python"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c73b943dc75f6f65e007c56ac6515c8f"

SRC_URI[md5sum] = "0b85b418018746d63ed66b55465697a9"
SRC_URI[sha256sum] = "cdca0a65ad48e08320672affe38c3dd4ea15e27821e5e1db9fa2b34299bdd41e"

SRC_URI += " file://0001-setup.py-Don-t-hardcode-swig-and-fann2-binary-locati.patch"

inherit pypi setuptools3

DEPENDS += "swig-native libfann"
