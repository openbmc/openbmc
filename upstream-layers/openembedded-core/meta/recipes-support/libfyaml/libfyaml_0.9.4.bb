SUMMARY = "A fully-featured YAML 1.2 and JSON parser/writer with zero-copy operation and no artificial limits"
HOMEPAGE = "https://github.com/pantoniou/libfyaml"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6399094fbc639a289cfca2d660c010aa"

GITHUB_BASE_URI = "https://github.com/pantoniou/libfyaml"
SRC_URI = "${GITHUB_BASE_URI}/releases/download/v${PV}/libfyaml-${PV}.tar.gz"
SRC_URI[sha256sum] = "dac2b0af7b757b32a4fa7c6493d85d0f7dea6effd20ae4352570b6a450b9e5fb"

inherit autotools pkgconfig github-releases

BBCLASSEXTEND = "native"
