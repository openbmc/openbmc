SUMMARY = "Berkeley LALR Yacc parser generator"
DESCRIPTION = "A parser generator utility that reads a grammar specification from a file and generates an LR(1) \
parser for it.  The parsers consist of a set of LALR(1) parsing tables and a driver routine written in the C \
programming language."
SECTION = "devel"
LICENSE = "PD"

SRC_URI = "ftp://invisible-island.net/byacc/byacc-${PV}.tgz \
           file://byacc-open.patch"

EXTRA_OECONF += "--program-transform-name='s,^,b,'"

BBCLASSEXTEND = "native"

inherit autotools

do_configure() {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
	oe_runconf
}
