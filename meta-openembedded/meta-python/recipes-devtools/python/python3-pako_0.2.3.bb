SUMMARY = "The universal package manager library"
HOMEPAGE = "https://github.com/MycroftAI/pako"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[md5sum] = "8eb7077075091c50e4b8a9f939607277"
SRC_URI[sha256sum] = "6be55fd8c5a2a6f02974f37438c1c47a3d9e764ce81c9d0a1a8c9a1815a59778"

inherit pypi setuptools3

do_install_append() {
    rm -rf ${D}/usr/share
}
