SUMMARY = "This is a filesystem client based on the SSH File Transfer Protocol using FUSE"
HOMEPAGE = "https://github.com/libfuse/sshfs"
SECTION = "console/network"
LICENSE = "GPL-2.0-only"
DEPENDS = "glib-2.0 fuse3"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/libfuse/sshfs;branch=master;protocol=https \
           file://03ee1f8aa0899268ec02b2f54849352df92a3a1d.patch \
           file://a1d58ae1be99571a88b8439b027abe6349b74658.patch \
"
SRCREV = "c91eb9a9a992f1a36c49a8e6f1146e45b5e1c8e7"
S = "${WORKDIR}/git"

inherit meson pkgconfig ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        python3-pytest \
        bash \
        fuse \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}
