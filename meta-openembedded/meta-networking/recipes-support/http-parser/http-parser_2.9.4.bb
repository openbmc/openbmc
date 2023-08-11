SUMMARY = "HTTP request/response parser for C"
DESCRIPTION = "This is a parser for HTTP messages written in C. It parses \
              both requests and responses. The parser is designed to be used \
              in performance HTTP applications. It does not make any \
              syscalls nor allocations, it does not buffer data, it can be \
              interrupted at anytime. Depending on your architecture, it \
              only requires about 40 bytes of data per message stream (in a \
              web server that is per connection)."
HOMEPAGE = "https://github.com/nodejs/http-parser"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=9bfa835d048c194ab30487af8d7b3778"

SRC_URI = "git://github.com/nodejs/http-parser.git;branch=master;protocol=https"
SRCREV = "2343fd6b5214b2ded2cdcf76de2bf60903bb90cd"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "PLATFORM=linux"

do_configure[noexec] = "1"

do_compile() {
    oe_runmake library package
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix} LIBDIR=${libdir}
}

BBCLASSEXTEND = "native nativesdk"
