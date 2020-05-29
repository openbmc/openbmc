SUMMARY = "versatile resource statics tool"
DESCRIPTION = "Dstat is a versatile replacement for vmstat, iostat, netstat and ifstat. \
Dstat overcomes some of their limitations and adds some extra features, more counters \
and flexibility. Dstat is handy for monitoring systems during performance tuning tests, \
benchmarks or troubleshooting."
HOMEPAGE = "http://dag.wiee.rs/home-made/dstat"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "asciidoc-native xmlto-native"

SRC_URI = "git://github.com/dagwieers/dstat.git \
           file://0001-change-dstat-to-python3.patch \
          "

SRCREV = "6f5db0aed26bf8cf2700d4ffe90a9bd3436ac728"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

RDEPENDS_${PN} += "python3-core python3-misc python3-resource python3-shell python3-unixadmin"
