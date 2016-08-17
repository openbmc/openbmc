SUMMARY = "versatile resource statics tool"
DESCRIPTION = "Dstat is a versatile replacement for vmstat, iostat, netstat and ifstat. \
Dstat overcomes some of their limitations and adds some extra features, more counters \
and flexibility. Dstat is handy for monitoring systems during performance tuning tests, \
benchmarks or troubleshooting."
HOMEPAGE = "http://dag.wiee.rs/home-made/dstat"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
SRC_URI = "git://github.com/dagwieers/dstat.git"
SRC_URI[md5sum] = "798e050e2e024f08a272dd4b0e1eba41"
SRC_URI[sha256sum] = "96d1e6ea2434e477fa97322d92778f68458d7e57bc55bc4f72e29467a52cffd1"

SRCREV = "5251397eb8d3b284a90bfdfaec0c8e1210146e3f"

S = "${WORKDIR}/git"

do_compile_prepend() {
    #undo the step "make docs"
    sed -i -e 's/$(MAKE) -C docs docs/# $(MAKE) -C docs docs/;' ${S}/Makefile
}
do_install() {
    oe_runmake 'DESTDIR=${D}' install
}
