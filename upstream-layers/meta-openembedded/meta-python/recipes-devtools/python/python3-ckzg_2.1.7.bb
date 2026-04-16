SUMMARY = "Python Bindings for the C-KZG Library"
HOMEPAGE = "https://github.com/ethereum/c-kzg-4844"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI += " \
	file://blst-0001-Support-64-bit-limbs-on-no-asm-platforms.patch \
"

SRC_URI[sha256sum] = "a0c61c5fd573af0267bcb435ef0f499911289ceb05e863480779ea284a3bb928"

inherit pypi setuptools3
