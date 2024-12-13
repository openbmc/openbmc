SUMMARY ="Reusable library for GPU-accelerated video/image rendering primitives"
LICENSE = "Apache-2.0 & BSD-3-Clause & BSL-1.0 & MIT"
LIC_FILES_CHKSUM = "file://3rdparty/Vulkan-Headers/LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://3rdparty/fast_float/LICENSE-APACHE;md5=81db248e90379bcfc0582b578b009bc3 \
                    file://3rdparty/fast_float/LICENSE-BOOST;md5=2c7a3fa82e66676005cd4ee2608fd7d2 \
                    file://3rdparty/fast_float/LICENSE-MIT;md5=32b11d50c7d9788d4270f6a83f3e68eb \
                    file://3rdparty/glad/LICENSE;md5=ae570f26774ac096cff8f992091a223c \
                    file://3rdparty/jinja/LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462 \
                    file://3rdparty/jinja/docs/license.rst;md5=5f4c795946979fabc2361be4c70d0a9f \
                    file://3rdparty/markupsafe/LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75 \
                    file://3rdparty/markupsafe/docs/license.rst;md5=5f4c795946979fabc2361be4c70d0a9f \
                    file://LICENSE;md5=435ed639f84d4585d93824e7da3d85da \
                    file://demos/3rdparty/nuklear/src/LICENSE;md5=6052431ae6cd4f0082276c54996e7770 \
                    file://demos/LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI = "gitsm://github.com/haasn/libplacebo;protocol=https;branch=master"

SRCREV = "1fd3c7bde7b943fe8985c893310b5269a09b46c5"

inherit meson pkgconfig

S = "${WORKDIR}/git"
