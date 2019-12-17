DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae77b0d903a788cb48f4f0926ffc468b"

SRCREV = "7aff80da2ca4fcb0b47f32ba684ff2e1cd48c579"
SRC_URI = "git://github.com/taocpp/PEGTL.git;protocol=https;branch=2.x \
          "

inherit cmake

S = "${WORKDIR}/git"
