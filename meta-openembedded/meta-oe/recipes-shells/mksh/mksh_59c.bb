DESCRIPTION = "The MirBSD Korn Shell - an enhanced version of the public domain ksh"
HOMEPAGE = "http://www.mirbsd.org/mksh.htm"
SECTION = "base/shell"

LICENSE = "${@bb.utils.contains("TCLIBC", "glibc", "MirOS & ISC", "MirOS", d)}"
LIC_FILES_CHKSUM = "file://main.c;beginline=6;endline=26;md5=6efc2c249328e4d2bd3e595d5b1f9d31 \
                    file://strlcpy.c;beginline=1;endline=17;md5=d953f28f0c43ee29e238ec9bc15df2a0 \
                   "

SRC_URI = "http://www.mirbsd.org/MirOS/dist/mir/${BPN}/${BPN}-R${PV}.tgz"

SRC_URI[sha256sum] = "77ae1665a337f1c48c61d6b961db3e52119b38e58884d1c89684af31f87bc506"

UPSTREAM_CHECK_REGEX = "${BPN}-R(?P<pver>.*)\.tgz"

inherit update-alternatives

S = "${WORKDIR}/${BPN}"

ALTERNATIVE:${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/${BPN}"
ALTERNATIVE_PRIORITY = "100"

do_compile() {
    sh ${S}/Build.sh -r
}

do_install() {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/mksh ${D}${base_bindir}/mksh

    install -d ${D}${sysconfdir}/skel
    install -m 0644 ${S}/dot.mkshrc ${D}${sysconfdir}/skel/.mkshrc
}

RPROVIDES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sh', '', d)}"
