SUMMARY = "The universal package manager library"
HOMEPAGE = "https://github.com/MycroftAI/pako"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "c033a073bb818ca336ae1fccba2655bd60dfe77744f85d4517abd3160d72231f"

inherit pypi setuptools3

do_install:append() {
  rm -rf ${D}${prefix}/pako/LICENSE
  rm -rf ${D}${prefix}/pako
}

RDEPENDS:${PN} += " \
    python3-appdirs \
    python3-io \
    python3-json \
    python3-logging \
"
