SUMMARY = "Module for serializing and de-serializing Java objects."
DESCRIPTION = "python-javaobj is a python library that provides functions\
 for reading and writing (writing is WIP currently) Java objects serialized\
 or will be deserialized by ObjectOutputStream. This form of object\
 representation is a standard data interchange format in Java world."
HOMEPAGE = "https://github.com/tcalmant/python-javaobj"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d273d63619c9aeaf15cdaf76422c4f87"

SRC_URI[sha256sum] = "ed3b1ffcfd39f165729cb53587cca02ab0bfa4c332c837a92d5ffe6aef5c8010"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
