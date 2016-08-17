SUMMARY = "Python 3.3+'s ipaddress for Python 2.6, 2.7, 3.2."
HOMEPAGE = "https://github.com/phihag/ipaddress"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

DEPENDS += "python-pip"

SRC_URI[md5sum] = "1e27b62aa20f5b6fc200b2bdbf0d0847"
SRC_URI[sha256sum] = "5a3182b322a706525c46282ca6f064d27a02cffbd449f9f47416f1dc96aa71b0"

inherit pypi setuptools
