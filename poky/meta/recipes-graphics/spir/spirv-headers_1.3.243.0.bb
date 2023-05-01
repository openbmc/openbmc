SUMMARY = "Machine-readable files for the SPIR-V Registry"
DESCRIPTION = "Headers are provided in the include directory, with up-to-date \
headers in the unified1 subdirectory. Older headers are provided according to \
their version."
SECTION = "graphics"
HOMEPAGE = "https://www.khronos.org/registry/spir-v"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c938b85bceb8fb26c1a807f28a52ae2d"

SRCREV = "1feaf4414eb2b353764d01d88f8aa4bcc67b60db"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Headers;protocol=https;branch=main"
PE = "1"
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native nativesdk"
