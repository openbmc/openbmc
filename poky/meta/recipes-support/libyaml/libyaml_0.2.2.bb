SUMMARY = "LibYAML is a YAML 1.1 parser and emitter written in C."
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, \
a human-readable data serialization format. "
HOMEPAGE = "https://pyyaml.org/wiki/LibYAML"
SECTION = "libs/devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a76b4c69bfcf82313bbdc0393b04438a"

SRC_URI = "https://pyyaml.org/download/libyaml/yaml-${PV}.tar.gz"
SRC_URI[md5sum] = "54bf11ccb8bc488b5b3bec931f5b70dc"
SRC_URI[sha256sum] = "4a9100ab61047fd9bd395bcef3ce5403365cafd55c1e0d0299cde14958e47be9"

S = "${WORKDIR}/yaml-${PV}"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
