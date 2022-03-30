SUMMARY = "User-space front-end command-line tool for ftrace"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=873f48a813bded3de6ebc54e6880c4ac"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git;branch=master \
	file://0001-trace-cmd-make-it-build-with-musl.patch"

SRCREV = "530b1a0caef39466e16bbd49de5afef89656f03f"

S = "${WORKDIR}/git"

do_install() {
       oe_runmake etcdir=${sysconfdir} DESTDIR=${D} install
       mkdir -p ${D}${libdir}/traceevent/plugins/${BPN}
       mv ${D}/${libdir}/traceevent/plugins/*.so ${D}${libdir}/traceevent/plugins/${BPN}/
}

FILES:${PN} += "${libdir}/traceevent/plugins"
