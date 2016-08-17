SUMMARY = "protobuf"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "http://code.google.com/p/protobuf/"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=af6809583bfde9a31595a58bb4a24514"

PR = "r0"

SRC_URI[md5sum] = "af05b2cca289f7b86eef2734a0cdc8b9"
SRC_URI[sha256sum] = "2667b7cda4a6bc8a09e5463adf3b5984e08d94e72338277affa8594d8b6e5cd1"
SRC_URI = "https://github.com/google/protobuf/archive/v2.6.1.tar.gz;downloadfilename=protobuf-2.6.1.tar.gz \
	"

EXTRA_OECONF += " --with-protoc=echo --disable-shared"

inherit native autotools

