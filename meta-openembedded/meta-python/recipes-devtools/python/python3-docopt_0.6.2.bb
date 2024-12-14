SUMMARY = "Pythonic argument parser, that will make you smile"
HOMEPAGE = "http://docopt.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=09b77fb74986791a3d4a0e746a37d88f"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "49b3a825280bd66b3aa83585ef59c4a8c82f2c8a522dbe754a8bc8d08c85c491"

BBCLASSEXTEND = "native nativesdk"
