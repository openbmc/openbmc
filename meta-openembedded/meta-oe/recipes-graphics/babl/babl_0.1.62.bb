SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

inherit gnomebase

SRC_URI = "http://ftp.gimp.org/pub/${BPN}/0.1/${BP}.tar.bz2"
SRC_URI[md5sum] = "28fa9d43549378ceebe2871d7721dd46"
SRC_URI[sha256sum] = "dc279f174edbcb08821cf37e4ab0bc02e6949369b00b150c759a6c24bfd3f510"

FILES_${PN} += "${libdir}/babl-*/*.so"
FILES_${PN}-dev += "${libdir}/babl-*/*.la"
FILES_${PN}-dbg += "${libdir}/babl-*/.debug/"
