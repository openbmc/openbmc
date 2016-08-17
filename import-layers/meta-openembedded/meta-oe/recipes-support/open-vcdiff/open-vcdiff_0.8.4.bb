SUMMARY = "An encoder/decoder for the VCDIFF (RFC3284) format"
DESCRIPTION = "A library with a simple API is included, as well as a \
               command-line executable that can apply the encoder and \
               decoder to source, target, and delta files. \
               A slight variation from the draft standard is defined \
               to allow chunk-by-chunk decoding when only a partial \
               delta file window is available."
HOMEPAGE = "http://code.google.com/p/open-vcdiff/"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff820d4ddc1ba05b6fd37b41a21506f9"

SRC_URI = "https://drive.google.com/uc?id=0B5WpIi2fQU1aNGJwVE9hUjU5clU&export=download;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "5c0d378d907bebc38b51c3d7e4117011"
SRC_URI[sha256sum] = "2b142b1027fb0a62c41347600e01a53fa274dad15445a7da48083c830c3138b3"

inherit autotools
