SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb5ea0d651f4c3519327171906045775"

SRC_URI = "https://github.com/libuv/${BPN}/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"

SRC_URI[md5sum] = "15550a9f5f45f7f32ae2d9bb0a21a2a7"
SRC_URI[sha256sum] = "6ec7eec6ecc24b1a8ffedebedb2fe9313fffb5410de89aaf784dd01080411c7a"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
