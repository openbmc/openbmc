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

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=4;endline=7;md5=e94ab3b72335e3cdadd6c1ff736dd714"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/J/JR/JROGERS/Net-Telnet-${PV}.tar.gz"
SRC_URI[md5sum] = "c8573c57a2d9469f038c40ac284b1e5f"
SRC_URI[sha256sum] = "677f68ba2cd2a824fae323fa82e183bf7e3d03c3c499c91d923bd6283796a743"

S = "${WORKDIR}/Net-Telnet-${PV}"

inherit cpan

RDEPENDS:${PN} = "perl"
