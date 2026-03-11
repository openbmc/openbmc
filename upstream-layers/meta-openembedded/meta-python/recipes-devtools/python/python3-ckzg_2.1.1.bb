SUMMARY = "Python Bindings for the C-KZG Library"
HOMEPAGE = "https://github.com/ethereum/c-kzg-4844"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI += " \
	file://blst-0001-Support-64-bit-limbs-on-no-asm-platforms.patch \
	file://python-ckzg-0001-Let-override-CC.patch \
	file://python-ckzg-0002-Disable-Werror.patch \
"

SRC_URI[sha256sum] = "d6b306b7ec93a24e4346aa53d07f7f75053bc0afc7398e35fa649e5f9d48fcc4"

inherit pypi setuptools3
