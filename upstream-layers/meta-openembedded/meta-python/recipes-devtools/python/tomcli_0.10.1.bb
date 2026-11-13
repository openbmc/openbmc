SUMMARY = "CLI for working with TOML files"
HOMEPAGE = "https://tomcli.gtmx.me"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3c5eb6b1eb58832012789763a45d051f"

SRC_URI[sha256sum] = "0a27c685620b10a0f389b1d55dc66ea904054b178176d41440de55d296e1109d"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} = "python3-click python3-tomlkit"

# NOTE: There are lots of skips in the ptest from tomli and tomli_w
# but we only care about the tomlkit backend.

BBCLASSEXTEND = "native nativesdk"
