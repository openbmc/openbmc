SUMMARY = "Python Bindings for the C-KZG Library"
HOMEPAGE = "https://github.com/ethereum/c-kzg-4844"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI += " \
	file://blst-0001-Support-64-bit-limbs-on-no-asm-platforms.patch \
"

SRC_URI[sha256sum] = "d7bef6b425dca6995457fc59fc5b30211d9b28cbbeee0e7a7bef1372e13f29ca"

inherit pypi setuptools3
