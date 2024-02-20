SUMMARY = "Generate JSON-RPC requests and parse responses in Python"
HOMEPAGE = "https://github.com/explodinglabs/jsonrpcclient"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=10f3d7679914df805c98fb351172e677"

SRCREV = "e5dd11736925a9a8e463fc9420eab95235f181e3"
SRC_URI = "git://github.com/explodinglabs/jsonrpcclient.git;branch=main;protocol=https"

inherit python_setuptools_build_meta

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "\
    python3-json \
    python3-math \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
