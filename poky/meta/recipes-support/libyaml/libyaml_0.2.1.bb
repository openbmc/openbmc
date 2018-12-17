SUMMARY = "LibYAML is a YAML 1.1 parser and emitter written in C."
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, \
a human-readable data serialization format. "
HOMEPAGE = "http://pyyaml.org/wiki/LibYAML"
SECTION = "libs/devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5591701d32590f9fa94f3bfee820b634"

SRC_URI = "http://pyyaml.org/download/libyaml/yaml-${PV}.tar.gz"
SRC_URI[md5sum] = "72724b9736923c517e5a8fc6757ef03d"
SRC_URI[sha256sum] = "78281145641a080fb32d6e7a87b9c0664d611dcb4d542e90baf731f51cbb59cd"

S = "${WORKDIR}/yaml-${PV}"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
