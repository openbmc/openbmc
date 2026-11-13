SUMMARY = "A drop-in replacement for argparse that allows options to also be set via config files and/or environment variables."
HOMEPAGE = "https://github.com/bw2/ConfigArgParse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da746463714cc35999ed9a42339f2943"

SRC_URI[sha256sum] = "607bea276a219912158afa1e5a716c3f8f88d542f9997dfd43bbd0b492a9f5a6"


inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

PACKAGECONFIG ?= "yaml toml"
PACKAGECONFIG[yaml] = ",,,python3-pyyaml"
PACKAGECONFIG[toml] = ",,,python3-toml"

RDEPENDS:${PN} += "\
    python3-core \
    python3-shell \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
