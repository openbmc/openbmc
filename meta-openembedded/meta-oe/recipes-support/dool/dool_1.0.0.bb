SUMMARY = "versatile resource statics tool"
DESCRIPTION = "Dstat is a versatile replacement for vmstat, iostat, netstat and ifstat. \
Dstat overcomes some of their limitations and adds some extra features, more counters \
and flexibility. Dstat is handy for monitoring systems during performance tuning tests, \
benchmarks or troubleshooting."
HOMEPAGE = "http://dag.wiee.rs/home-made/dstat"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "asciidoc-native xmlto-native"

SRC_URI = "git://github.com/scottchiefbaker/dool.git;branch=master;protocol=https \
	   file://0001-Fix-build-error-as-following.patch \
	   file://0001-Fix-rename-in-docs.patch \
          "

SRCREV = "34a3244b46aa70a31f871a7ca8ffa8d3a7b950d2"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

RDEPENDS:${PN} += "python3-core python3-misc python3-resource python3-shell python3-six python3-unixadmin"
