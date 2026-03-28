SUMMARY = "Optimised Inner Loop Runtime Compiler"
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/orc.html"
DESCRIPTION = "Optimised Inner Loop Runtime Compiler is a Library and set of tools for compiling and executing SIMD assembly language-like programs that operate on arrays of data."
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1400bd9d09e8af56b9ec982b3d85797e"

SRC_URI = "http://gstreamer.freedesktop.org/src/orc/orc-${PV}.tar.xz"
SRC_URI[sha256sum] = "7ec912ab59af3cc97874c456a56a8ae1eec520c385ec447e8a102b2bd122c90c"

inherit meson pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[hotdoc] = "-Dhotdoc=enabled,-Dhotdoc=disabled,hotdoc-native"

# distinguish from apache:orc
CVE_PRODUCT = "gstreamer:orc"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "orc-examples"
PACKAGES_DYNAMIC += "^liborc-.*"
FILES:orc-examples = "${libdir}/orc/*"
FILES:${PN} = "${bindir}/*"

python populate_packages:prepend () {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, r'^lib(.*)\.so\.*', 'lib%s', 'ORC %s library', extra_depends='', allow_links=True)
}

do_compile:prepend:class-native () {
    sed -i -e 's#/tmp#.#g' ${S}/orc/orccodemem.c
}

#Add this function to deal with QA Issue like File /usr/share/doc/orc/html/assets/js/search/hotdoc_fragments/orctarget.html-enum (unnamed at /work/x86-64-v3-poky-linux/orc/0.4.42/sources/orc-0.4.42/orc/orctarget.h:39:1).fragment in package orc-doc contains reference to TMPDIR [buildpaths]
do_install:append:class-target () {
    if ${@bb.utils.contains('PACKAGECONFIG', 'hotdoc', 'true', 'false', d)}; then
        rm -rf ${D}${docdir}/${PN}/html/assets/js/search/hotdoc_fragments/*unnamed*
        for ss in $(find ${D}${docdir}/${PN}/html -type f); do
            sed -i 's,${S},,g' "$ss"
        done
    fi
}
