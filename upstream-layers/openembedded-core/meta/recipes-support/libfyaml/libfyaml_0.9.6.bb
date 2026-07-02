SUMMARY = "A fully-featured YAML 1.2 and JSON parser/writer with zero-copy operation and no artificial limits"
HOMEPAGE = "https://github.com/pantoniou/libfyaml"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6399094fbc639a289cfca2d660c010aa"

GITHUB_BASE_URI = "https://github.com/pantoniou/libfyaml"
SRC_URI = "${GITHUB_BASE_URI}/releases/download/v${PV}/libfyaml-${PV}.tar.gz \
           file://0001-Fix-32-bit-build-by-removing-stray-parameter-to-fy_s.patch \
           file://0002-vlsize-Handle-decoding-when-size_t-sizeof-uint64_t.patch \
           file://0001-build-don-t-output-none-required-to-LIBM-if-no-linke.patch \
           "
SRC_URI[sha256sum] = "a59cc3331e2eb903ec36933ad52a45888041cac31e44f553a00511131242c483"

inherit autotools pkgconfig github-releases

BBCLASSEXTEND = "native"
