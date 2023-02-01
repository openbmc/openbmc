SUMMARY = "User-space front-end command-line tool for ftrace"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd \
    "

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git;branch=master \
           file://0001-Replace-LFS64-interfaces-off64_t-and-lseek64.patch \
           file://0002-Drop-using-_LARGEFILE64_SOURCE.patch \
           file://0001-Do-not-emit-useless-rpath.patch"
SRCREV = "18233e4c32857cb7ddd4960beeec8360ed834fc5"

S = "${WORKDIR}/git"

DEPENDS += "libtraceevent libtracefs zstd xmlto-native asciidoc-native swig-native bison-native flex-native"

inherit pkgconfig bash-completion

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
        oe_runmake libdir_relative=${BASELIB} libs
        oe_runmake libdir_relative=${BASELIB} all
}

do_install() {
       oe_runmake libdir_relative=${baselib} etcdir=${sysconfdir} pkgconfig_dir=${libdir}/pkgconfig DESTDIR=${D} install install_libs
       # Because makefile uses cp instead of install we need to change owner of files
       chown -R root:root ${D}${libdir}
}
