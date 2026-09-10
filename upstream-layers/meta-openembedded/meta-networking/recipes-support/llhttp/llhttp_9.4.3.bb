SUMMARY = "Port of http_parser to llvm's libFuzzer, and a rewrite of the HTTP parser used in Node.js"
DESCRIPTION = "llhttp is a HTTP/1.x parser generated from a bytecode-like \
               instruction set that is smaller and faster to maintain than \
               the legacy http_parser it replaces. It is used by restinio \
               (and Node.js itself) as the HTTP request/response parser."
HOMEPAGE = "https://github.com/nodejs/llhttp"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e4e583cb28b7e0c2ef8df4832706e96"

# The release/v* tags carry the generated sources and live on the "release"
# branch, which shares no history with "main".
SRC_URI = "git://github.com/nodejs/llhttp.git;protocol=https;branch=release;tag=release/v${PV}"
SRCREV = "0e815792b167a9bd8ace259b95b7da953776c288"

inherit cmake

BBCLASSEXTEND = "native nativesdk"
