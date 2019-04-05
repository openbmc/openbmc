DESCRIPTION = "The MirBSD Korn Shell - an enhanced version of the public domain ksh"
HOMEPAGE = "http://www.mirbsd.org/mksh.htm"
SECTION = "base/shell"

LICENSE = "${@bb.utils.contains("TCLIBC", "glibc", "MirOS & ISC", "MirOS", d)}"
LIC_FILES_CHKSUM = "file://main.c;beginline=6;endline=25;md5=7204fec4d12912f2a13fe8745bc356f9 \
                    file://strlcpy.c;beginline=1;endline=17;md5=d953f28f0c43ee29e238ec9bc15df2a0 \
                   "

SRC_URI = "http://www.mirbsd.org/MirOS/dist/mir/mksh/mksh-R56c.tgz"

SRC_URI[md5sum] = "4799a9ac6d55871d79ba66713d928663"
SRC_URI[sha256sum] = "dd86ebc421215a7b44095dc13b056921ba81e61b9f6f4cdab08ca135d02afb77"

inherit update-alternatives

S = "${WORKDIR}/${BPN}"

ALTERNATIVE_${PN} = "sh"
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

RPROVIDES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sh', '', d)}"
