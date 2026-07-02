SUMMARY = "Data::HexDump - Hexadecial Dumper"
DESCRIPTION = "Dump in hexadecimal the content of a scalar. The result \
is returned in a string. Each line of the result consists of the offset \
in the source in the leftmost column of each line, followed by one or \
more columns of data from the source in hexadecimal. The rightmost column \
of each line shows the printable characters \
(all others are shown as single dots).\
"

HOMEPAGE = "https://metacpan.org/pod/Data::HexDump"
SECTION = "libs"

LICENSE = "Artistic-1.0"
LIC_FILES_CHKSUM = "file://lib/Data/HexDump.pm;beginline=277;endline=279;md5=bf1cd9373f8d1f85fe091ee069a480e9"

DEPENDS = "perl"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEILB/Data-HexDump-${PV}.tar.gz \
           file://run-ptest \
"
SRC_URI[sha256sum] = "bc36f404438ac36ad2b9295539227d36f99cd1623f1e347af77c594c40ccbcf8"

S = "${UNPACKDIR}/Data-HexDump-${PV}"

inherit cpan ptest update-alternatives

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/t
    install -m 0755 ${B}/t/* ${D}${PTEST_PATH}/t
}

RDEPENDS:${PN}-ptest += "perl-module-exporter perl-module-carp perl-module-filehandle"

BBCLASSEXTEND = "native"

ALTERNATIVES_PRIORITY = "100"
ALTERNATIVE:${PN} = "hexdump"
ALTERNATIVE_LINK_NAME[hexdump] = "${bindir}/hexdump"

