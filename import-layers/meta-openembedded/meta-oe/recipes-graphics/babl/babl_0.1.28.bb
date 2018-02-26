SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

inherit gnomebase

SRC_URI = "http://ftp.gimp.org/pub/${BPN}/0.1/${BP}.tar.bz2"
SRC_URI[md5sum] = "cc53d8474a43aafb7cdaccea56cfde44"
SRC_URI[sha256sum] = "63f3ed23e72a857a0e6df53d9d968a325024177b01edbe314a0c98b499eb8603"

FILES_${PN} += "${libdir}/babl-*/*.so"
FILES_${PN}-dev += "${libdir}/babl-*/*.la"
FILES_${PN}-dbg += "${libdir}/babl-*/.debug/"
