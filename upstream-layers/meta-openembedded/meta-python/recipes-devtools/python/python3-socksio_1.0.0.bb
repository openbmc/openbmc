SUMMARY = "Sans-I/O implementation of SOCKS4, SOCKS4A, and SOCKS5."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0b4dda8edb09eec767aaded3cbf8f6b1"

inherit pypi python_flit_core

SRC_URI += "file://0001-Unpin-flit-core-dependency.patch"

SRC_URI[sha256sum] = "f88beb3da5b5c38b9890469de67d0cb0f9d494b78b106ca1845f96c10b91c4ac"
