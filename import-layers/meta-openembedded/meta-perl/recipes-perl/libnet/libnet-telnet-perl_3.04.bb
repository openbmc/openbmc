SUMMARY = "Net-Telnet Perl module"
DESCRIPTION = "Net::Telnet allows you to make client connections to a TCP port and do \
network I/O, especially to a port using the TELNET protocol. Simple I/O \
methods such as print, get, and getline are provided. More sophisticated \
interactive features are provided because connecting to a TELNET port \
ultimately means communicating with a program designed for human interaction. \
These interactive features include the ability to specify a time-out and to \
wait for patterns to appear in the input stream, such as the prompt from a \
shell."

HOMEPAGE = "http://search.cpan.org/dist/Net-Telnet/"
SECTION = "Development/Libraries"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=4;endline=7;md5=3fd238bfb6ee1810cb15d5d95e07b0f5"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/J/JR/JROGERS/Net-Telnet-${PV}.tar.gz"
SRC_URI[md5sum] = "d2514080116c1b0fa5f96295c84538e3"
SRC_URI[sha256sum] = "e64d567a4e16295ecba949368e7a6b8b5ae2a16b3ad682121d9b007dc5d2a37a"

S = "${WORKDIR}/Net-Telnet-${PV}"

inherit cpan

RDEPENDS_${PN} = "perl"
