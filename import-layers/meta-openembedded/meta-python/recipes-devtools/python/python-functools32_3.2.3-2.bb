SUMMARY = "Backport of the functools module from Python 3.2.3 for use on 2.7 and PyPy."
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27cf2345969ed18e6730e90fb0063a10"

SRC_URI[md5sum] = "09f24ffd9af9f6cd0f63cb9f4e23d4b2"
SRC_URI[sha256sum] = "f6253dfbe0538ad2e387bd8fdfd9293c925d63553f5813c4e587745416501e6d"

inherit pypi setuptools

BBCLASSEXTEND = "native nativesdk"
