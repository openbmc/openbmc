SUMMARY = "versatile resource statics tool"
DESCRIPTION = "Dstat is a versatile replacement for vmstat, iostat, netstat and ifstat. \
Dstat overcomes some of their limitations and adds some extra features, more counters \
and flexibility. Dstat is handy for monitoring systems during performance tuning tests, \
benchmarks or troubleshooting."
HOMEPAGE = "http://dag.wiee.rs/home-made/dstat"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "asciidoc-native xmlto-native"

SRC_URI = "git://github.com/dagwieers/dstat.git"

SRCREV = "ebace6d4177f8748f35cec87f7a49946046b0a20"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

RDEPENDS_${PN} += "python-core python-misc python-resource python-shell python-unixadmin"
