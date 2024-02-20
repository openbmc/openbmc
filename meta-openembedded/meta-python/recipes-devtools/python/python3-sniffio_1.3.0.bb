SUMMARY = "Sniff out which async library your code is running under"
SECTION = "devel/python"
LICENSE = "MIT | Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=fa7b86389e58dd4087a8d2b833e5fe96 \
    file://LICENSE.MIT;md5=e62ba5042d5983462ad229f5aec1576c \
    file://LICENSE.APACHE2;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e60305c5e5d314f5389259b7f22aaa33d8f7dee49763119234af3755c55b9101"
