SUMMARY = "Optimised Inner Loop Runtime Compiler"
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/orc.html"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1400bd9d09e8af56b9ec982b3d85797e"

SRC_URI = "http://gstreamer.freedesktop.org/src/orc/orc-${PV}.tar.xz"
SRC_URI[md5sum] = "25799917c7d31a891d5e32b83ad08f6d"
SRC_URI[sha256sum] = "4f8901f9144b5ec17dffdb33548b5f4c7f8049b0d1023be3462cdd64ec5a3ab2"

inherit autotools pkgconfig gtk-doc

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "orc-examples"
PACKAGES_DYNAMIC += "^liborc-.*"
FILES_orc-examples = "${libdir}/orc/*"
FILES_${PN} = "${bindir}/*"

python populate_packages_prepend () {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, r'^lib(.*)\.so\.*', 'lib%s', 'ORC %s library', extra_depends='', allow_links=True)
}

do_compile_prepend_class-native () {
    sed -i -e 's#/tmp#.#g' ${S}/orc/orccodemem.c
}
