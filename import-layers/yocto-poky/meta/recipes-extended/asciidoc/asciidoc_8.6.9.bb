SUMMARY = "Tool for creating HTML, PDF, EPUB, man pages"
DESCRIPTION = "AsciiDoc is a text document format for writing short documents, \
articles, books and UNIX man pages."

HOMEPAGE = "http://asciidoc.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://COPYRIGHT;md5=029ad5428ba5efa20176b396222d4069"

SRC_URI = "http://downloads.sourceforge.net/project/${BPN}/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "c59018f105be8d022714b826b0be130a"
SRC_URI[sha256sum] = "78db9d0567c8ab6570a6eff7ffdf84eadd91f2dfc0a92a2d0105d323cab4e1f0"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/asciidoc/files/"

inherit autotools-brokensep

export DESTDIR = "${D}"
DEPENDS_class-native = "docbook-xml-dtd4-native"
RDEPENDS_${PN} += "python" 
BBCLASSEXTEND = "native"

CLEANBROKEN = "1"
