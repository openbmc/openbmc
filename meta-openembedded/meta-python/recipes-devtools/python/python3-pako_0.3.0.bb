SUMMARY = "The universal package manager library"
HOMEPAGE = "https://github.com/MycroftAI/pako"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "eabd1c121d6701069d1a10132f197ee2b5f4e75a3d68a93b07f69214ab0ff9c7"

inherit pypi setuptools3

do_install_append() {
    rm -rf ${D}/usr/share
}
