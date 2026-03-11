SUMMARY = "fast_float number parsing library: 4x faster than strtod"
HOMEPAGE = "https://github.com/fastfloat/fast_float"
LICENSE = "Apache-2.0 & BSL-1.0 & MIT"
LIC_FILES_CHKSUM = " \
	file://LICENSE-MIT;md5=32b11d50c7d9788d4270f6a83f3e68eb \
	file://LICENSE-APACHE;md5=81db248e90379bcfc0582b578b009bc3 \
	file://LICENSE-BOOST;md5=2c7a3fa82e66676005cd4ee2608fd7d2 \
"

SRC_URI = "git://github.com/fastfloat/fast_float.git;protocol=https;branch=main"

SRCREV = "50a80a73ab2ab256ba1c3bf86923ddd8b4202bc7"

inherit cmake
