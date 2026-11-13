SUMMARY = "Dynamic version generation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=059eed55dbfd3fea022510ea62c95dc1"

SRC_URI[sha256sum] = "fe2f732186710ee5048044fdbef6c76c6afbb9ee7de4f6d00cf34256e9ec2e7f"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"
