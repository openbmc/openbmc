SUMMARY = "C++ headers for C extension development"
HOMEPAGE = "https://cppy.readthedocs.io/en/latest/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0bfb3e39b13587f0028f17baf0e42371"

SRC_URI[md5sum] = "2110891d75aa12551deebba1603428c6"
SRC_URI[sha256sum] = "4eda6f1952054a270f32dc11df7c5e24b259a09fddf7bfaa5f33df9fb4a29642"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
