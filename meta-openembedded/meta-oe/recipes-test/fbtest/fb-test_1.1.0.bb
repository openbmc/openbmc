SUMMARY = "Test suite for Linux framebuffer"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRCREV = "063ec650960c2d79ac51f5c5f026cb05343a33e2"
SRC_URI = "git://github.com//ponty/fb-test-app.git;branch=master;protocol=https"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 fb-test ${D}${bindir}
    # avoid collisions with perf (perf) and mesa-demos (offset)
    for prog in perf rect offset ; do
        install -m 0755 $prog ${D}${bindir}/fb-$prog
    done
}
