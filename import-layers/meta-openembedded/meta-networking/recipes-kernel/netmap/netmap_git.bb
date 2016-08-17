require netmap.inc

DEPENDS = "netmap-modules"
PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OECONF = "--kernel-dir=${STAGING_KERNEL_BUILDDIR} \
                --kernel-sources=${STAGING_KERNEL_DIR} \
                --no-drivers \
                --disable-generic \
                --prefix=${prefix} \
                --destdir=${D} \
                --cc='${CC}' \
                --ld='${LD}' \
                "
SRC_URI += "file://0001-testmmap-fix-compile-issue-with-gcc-5.x.patch"

do_configure () {
    cd ${S}/LINUX
    ./configure ${EXTRA_OECONF}
}

do_compile () {
    cd ${S}/LINUX
    make apps
}

do_install () {
    cd ${S}/LINUX
    make install-apps DESTDIR=${D}
}

FILES_${PN} += "${bindir}"
RDEPENDS_${PN} = "kernel-module-netmap"
RRECOMMENDS_${PN} = "kernel-module-netmap"
