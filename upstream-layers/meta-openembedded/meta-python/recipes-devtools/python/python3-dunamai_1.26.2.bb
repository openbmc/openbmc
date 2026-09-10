SUMMARY = "Dynamic version generation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=059eed55dbfd3fea022510ea62c95dc1"

SRC_URI[sha256sum] = "84ea45eddf9bb4b40df7610b1b22a03137365e6257dbf9d7b72128fdccca564c"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"
