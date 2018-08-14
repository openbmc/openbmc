SUMMARY = "Module for serializing and de-serializing Java objects."
DESCRIPTION = "python-javaobj is a python library that provides functions\
 for reading and writing (writing is WIP currently) Java objects serialized\
 or will be deserialized by ObjectOutputStream. This form of object\
 representation is a standard data interchange format in Java world."
HOMEPAGE = "https://github.com/tcalmant/python-javaobj"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://javaobj.py;beginline=21;endline=33;md5=8b504419ce78f8133b21b4d4a89ef7e9"

SRC_URI[md5sum] = "f669bb8099380e3dd84db3d542aa5b68"
SRC_URI[sha256sum] = "b8b0d310fd97e1c7914d7911ee9d3f4b3d84d2a674aa95a4b0da3d8f155fc1bb"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
