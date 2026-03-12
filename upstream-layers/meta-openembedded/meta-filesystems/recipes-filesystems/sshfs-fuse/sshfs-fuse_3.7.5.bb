SUMMARY = "This is a filesystem client based on the SSH File Transfer Protocol using FUSE"
HOMEPAGE = "https://github.com/libfuse/sshfs"
SECTION = "console/network"
LICENSE = "GPL-2.0-only"
DEPENDS = "glib-2.0 fuse3"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/libfuse/sshfs;branch=master;protocol=https;tag=sshfs-${PV}"
SRCREV = "9e35c39ba83f54a49a9df4bf0a629f26c60cc38c"

inherit meson pkgconfig ptest

SRC_URI += " \
	file://run-ptest \
"
#python3-compile for filecmp module
RDEPENDS:${PN}-ptest += " \
        python3-compile \
        python3-pytest \
        python3-unittest-automake-output \
        bash \
        fuse \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
        ln -sf ${bindir}/sshfs ${D}${PTEST_PATH}/sshfs
}
