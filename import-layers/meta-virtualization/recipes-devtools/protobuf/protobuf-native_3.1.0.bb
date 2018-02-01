SUMMARY = "protobuf"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "http://code.google.com/p/protobuf/"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=35953c752efc9299b184f91bef540095"

SRC_URI[md5sum] = "14a532a7538551d5def317bfca41dace"
SRC_URI[sha256sum] = "0a0ae63cbffc274efb573bdde9a253e3f32e458c41261df51c5dbc5ad541e8f7"
SRC_URI = "https://github.com/google/protobuf/archive/v3.1.0.tar.gz;downloadfilename=protobuf-3.1.0.tar.gz \
	"

EXTRA_OECONF += " --with-protoc=echo"

inherit native autotools

