SUMMARY = "Dynamic version generation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=059eed55dbfd3fea022510ea62c95dc1"

SRC_URI[sha256sum] = "3b46007bd65b00b4824ead0a1aee365fd22d0ec2b9c219497d4fd48f52860c8b"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"
