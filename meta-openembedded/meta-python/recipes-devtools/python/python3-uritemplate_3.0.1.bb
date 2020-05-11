# This recipe is originally from meta-openstack:
# https://git.yoctoproject.org/cgit/cgit.cgi/meta-cloud-services/tree/meta-openstack/recipes-devtools/python/python3-uritemplate_3.0.0.bb?h=master

SUMMARY = "Simple python library to deal with URI Templates."
AUTHOR = "Ian Cordasco"
LICENSE = "Apache-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f6d769bdcfacac3c1a1ffa568937fe0"

SRC_URI[md5sum] = "869fb44fbd56713490db7272eb36c8ae"
SRC_URI[sha256sum] = "5af8ad10cec94f215e3f48112de2022e1d5a37ed427fbd88652fa908f2ab7cae"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
