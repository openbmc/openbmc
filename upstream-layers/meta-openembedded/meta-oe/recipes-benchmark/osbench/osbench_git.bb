SUMMARY = "Operating system benchmark tools"
DESCRIPTION = "A collection of micro-benchmarks that measure the performance of OS primitives"
HOMEPAGE = "https://gitlab.com/mbitsnbites/osbench"

LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://UNLICENSE;md5=7246f848faa4e9c9fc0ea91122d6e680"

SRC_URI = "git://gitlab.com/mbitsnbites/osbench.git;protocol=https;branch=master"
SRCREV = "c59c7c58649162c33250a326ca8fa582b520a29e"

inherit meson

MESON_SOURCEPATH = "${S}/src"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${B}/create_processes ${D}${bindir}/
    install -m 0755 ${B}/create_threads ${D}${bindir}/
    install -m 0755 ${B}/create_files ${D}${bindir}/
    install -m 0755 ${B}/launch_programs ${D}${bindir}/
    install -m 0755 ${B}/mem_alloc ${D}${bindir}/
}
