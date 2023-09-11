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
SRC_URI[sha256sum] = "eb872b4f0e1f0ebe59c9f7bd8c506c4204893ba6a8492de31df416f0d5170fd0"
PROVIDES:class-native += "gzip-native"

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

do_install:append:class-native() {
	install -d ${D}${bindir}
	install ${B}/pigz ${D}${bindir}/gzip
	ln -nsf gzip ${D}${bindir}/gunzip
	ln -nsf gzip ${D}${bindir}/zcat
}

ALTERNATIVE_PRIORITY = "110"
ALTERNATIVE:${PN} = "gunzip gzip zcat"
ALTERNATIVE:${PN}:class-nativesdk = ""
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[zcat] = "${base_bindir}/zcat"
ALTERNATIVE_TARGET = "${base_bindir}/pigz"

BBCLASSEXTEND = "native nativesdk"
