SUMMARY = "Pyflame: A Ptracing Profiler For Python"
HOMEPAGE = "https://github.com/uber/pyflame"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
DEPENDS = "python"

SRC_URI = "git://github.com/uber/pyflame.git;protocol=https"
S = "${WORKDIR}/git"

inherit pkgconfig autotools

COMPATIBLE_HOST_libc-musl_class-target = "null"
COMPATIBLE_HOST_mipsarch_class-target = "null"
COMPATIBLE_HOST_aarch64_class-target = "null"
