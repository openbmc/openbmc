SUMMARY = "Optimised Inner Loop Runtime Compiler"
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/orc.html"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1400bd9d09e8af56b9ec982b3d85797e"

SRC_URI = "http://gstreamer.freedesktop.org/src/orc/orc-${PV}.tar.xz"

SRC_URI[md5sum] = "5837dc20dacb5b668935bbded10cbb61"
SRC_URI[sha256sum] = "51e53e58fc8158e5986a1f1a49a6d970c5b16493841cf7b9de2c2bde7ce36b93"

inherit autotools pkgconfig gtk-doc

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "orc-examples"
PACKAGES_DYNAMIC += "^liborc-.*"
FILES_orc-examples = "${libdir}/orc/*"
FILES_${PN} = "${bindir}/*"

python populate_packages_prepend () {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, '^lib(.*)\.so\.*', 'lib%s', 'ORC %s library', extra_depends='', allow_links=True)
}

do_compile_prepend_class-native () {
    sed -i -e 's#/tmp#.#g' ${S}/orc/orccodemem.c
}
