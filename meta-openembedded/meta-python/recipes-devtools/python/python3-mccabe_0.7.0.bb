DESCRIPTION = "McCabe checker, plugin for flake8"
HOMEPAGE = "https://github.com/PyCQA/mccabe"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a489dc62bacbdad3335c0f160a974f0f"

SRC_URI[sha256sum] = "348e0240c33b60bbdf4e523192ef919f28cb2c3d7d5c7794f74009290f236325"

inherit pypi setuptools3

DEPENDS += "python3-pytest-runner-native"

BBCLASSEXTEND = "native nativesdk"
