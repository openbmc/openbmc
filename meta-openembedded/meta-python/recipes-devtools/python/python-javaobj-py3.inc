SUMMARY = "Module for serializing and de-serializing Java objects."
DESCRIPTION = "python-javaobj is a python library that provides functions\
 for reading and writing (writing is WIP currently) Java objects serialized\
 or will be deserialized by ObjectOutputStream. This form of object\
 representation is a standard data interchange format in Java world."
HOMEPAGE = "https://github.com/tcalmant/python-javaobj"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=15;endline=27;md5=af9ce26ac2de1b7436eb08c9308b4a1e"

SRC_URI[md5sum] = "1c6189a0d9aef51d74bfc52cd2953991"
SRC_URI[sha256sum] = "a57469e4f0d0eeba7482ad9839595db6e99ca4ac6630a4c8064dda75a2182549"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
