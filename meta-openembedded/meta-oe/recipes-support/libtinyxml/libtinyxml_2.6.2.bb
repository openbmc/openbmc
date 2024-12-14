# (c) Copyright 2012  Hewlett-Packard Development Company, L.P.

SUMMARY = "a simple, small, minimal, C++ XML parser"
HOMEPAGE = "http://www.sourceforge.net/projects/tinyxml"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://readme.txt;md5=f8f366f3370dda889f60faa7db162cf4"
SECTION = "libs"


SRC_URI = "${SOURCEFORGE_MIRROR}/tinyxml/tinyxml_${@'${PV}'.replace('.', '_')}.tar.gz \
           file://enforce-use-stl.patch \
           file://entity-encoding.patch"
SRC_URI[sha256sum] = "15bdfdcec58a7da30adc87ac2b078e4417dbe5392f3afb719f9ba6d062645593"

S = "${WORKDIR}/tinyxml"

EXTRA_CXXFLAGS = "-I. -fPIC"

do_compile() {
    ${CXX} ${CXXFLAGS} ${EXTRA_CXXFLAGS} -c -o tinyxml.o tinyxml.cpp
    ${CXX} ${CXXFLAGS} ${EXTRA_CXXFLAGS} -c -o tinyxmlerror.o tinyxmlerror.cpp
    ${CXX} ${CXXFLAGS} ${EXTRA_CXXFLAGS} -c -o tinyxmlparser.o tinyxmlparser.cpp
    ${CXX} ${CXXFLAGS} \
            -shared \
            -Wl,-soname,libtinyxml.so.${PV} \
            -o libtinyxml.so.${PV} \
            ${LDFLAGS} \
            tinyxml.o \
            tinyxmlparser.o \
            tinyxmlerror.o

}

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${S}/libtinyxml.so.${PV} ${D}${libdir}
    ln -sf libtinyxml.so.${PV} ${D}${libdir}/libtinyxml.so

    install -d ${D}${includedir}
    install -m 0644 ${S}/tinyxml.h ${D}${includedir}
}

BBCLASSEXTEND = "native"
