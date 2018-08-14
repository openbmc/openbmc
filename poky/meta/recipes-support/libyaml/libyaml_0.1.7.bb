SUMMARY = "LibYAML is a YAML 1.1 parser and emitter written in C."
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, \
a human-readable data serialization format. "
HOMEPAGE = "http://pyyaml.org/wiki/LibYAML"
SECTION = "libs/devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

SRC_URI = "http://pyyaml.org/download/libyaml/yaml-${PV}.tar.gz \
          "

SRC_URI[md5sum] = "1abf45bd3a96374fa55ca63b32f9f2f9"
SRC_URI[sha256sum] = "8088e457264a98ba451a90b8661fcb4f9d6f478f7265d48322a196cec2480729"

S = "${WORKDIR}/yaml-${PV}"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
