SUMMARY = "protobuf"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "http://code.google.com/p/protobuf/"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=35953c752efc9299b184f91bef540095"

PR = "r0"

SRC_URI[md5sum] = "d4f6ca65aadc6310b3872ee421e79fa6"
SRC_URI[sha256sum] = "f5b3563f118f1d3d6e001705fa7082e8fc3bda50038ac3dff787650795734146"
SRC_URI = "https://github.com/google/protobuf/archive/v3.0.0.tar.gz;downloadfilename=protobuf-3.0.0.tar.gz \
	"

EXTRA_OECONF += " --with-protoc=echo --disable-shared"

inherit native autotools

