SUMMARY = "A parallel implementation of gzip"
DESCRIPTION = "pigz, which stands for parallel implementation of gzip, is a \
fully functional replacement for gzip that exploits multiple processors and \
multiple cores to the hilt when compressing data. pigz was written by Mark \
Adler, and uses the zlib and pthread libraries."
HOMEPAGE = "http://zlib.net/pigz/"
SECTION = "console/utils"
LICENSE = "Zlib & Apache-2.0"
LIC_FILES_CHKSUM = "file://pigz.c;md5=9ae6dee8ceba9610596ed0ada493d142;beginline=7;endline=21"

SRC_URI = "http://zlib.net/${BPN}/fossils/${BP}.tar.gz"
SRC_URI[sha256sum] = "2eed7b0d7449d1d70903f2a62cd6005d262eb3a8c9e98687bc8cbb5809db2a7d"
PROVIDES_class-native += "gzip-native"

# Point this at the homepage in case /fossils/ isn't updated
UPSTREAM_CHECK_URI = "http://zlib.net/${BPN}/"
UPSTREAM_CHECK_REGEX = "pigz-(?P<pver>.*)\.tar"

DEPENDS = "zlib"

EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit update-alternatives

do_install() {
	# Install files into /bin (FHS), which is typical place for gzip
	install -d ${D}${base_bindir}
	install ${B}/pigz ${D}${base_bindir}/pigz
	ln -nsf pigz ${D}${base_bindir}/unpigz
	ln -nsf pigz ${D}${base_bindir}/pigzcat
}

do_install_append_class-native() {
	install -d ${D}${bindir}
	install ${B}/pigz ${D}${bindir}/gzip
	ln -nsf gzip ${D}${bindir}/gunzip
	ln -nsf gzip ${D}${bindir}/zcat
}

ALTERNATIVE_PRIORITY = "110"
ALTERNATIVE_${PN} = "gunzip gzip zcat"
ALTERNATIVE_${PN}_class-nativesdk = ""
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[zcat] = "${base_bindir}/zcat"
ALTERNATIVE_TARGET = "${base_bindir}/pigz"

BBCLASSEXTEND = "native nativesdk"
