SUMMARY  = "Timing analysis tools for suspend/resume/boot"
DESCRIPTION = "This tool suite is designed to assist kernel and OS developers \
in optimizing their linux stack's suspend/resume & boot time."
HOMEPAGE = "https://01.org/pm-graph"
LICENSE  = "GPL-2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRCREV = "cf59527dc24fdd2f314ae4dcaeb3d68a117988f6"
SRC_URI = "git://github.com/intel/pm-graph.git \
           file://0001-Makefile-fix-multilib-build-failure.patch \
           file://0001-sleepgraph.py-use-python3.patch \
"
S = "${WORKDIR}/git"

COMPATIBLE_HOST='(i.86|x86_64).*'
EXTRA_OEMAKE = "PREFIX=${prefix} DESTDIR=${D} BASELIB=${baselib}"

do_install() {
        oe_runmake install
}

RDEPENDS_${PN} += "python3-core python3-threading python3-datetime python3-compression"
