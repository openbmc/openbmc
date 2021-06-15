DESCRIPTION = "The MirBSD Korn Shell - an enhanced version of the public domain ksh"
HOMEPAGE = "http://www.mirbsd.org/mksh.htm"
SECTION = "base/shell"

LICENSE = "${@bb.utils.contains("TCLIBC", "glibc", "MirOS & ISC", "MirOS", d)}"
LIC_FILES_CHKSUM = "file://main.c;beginline=6;endline=26;md5=6efc2c249328e4d2bd3e595d5b1f9d31 \
                    file://strlcpy.c;beginline=1;endline=17;md5=d953f28f0c43ee29e238ec9bc15df2a0 \
                   "

SRC_URI = "http://www.mirbsd.org/MirOS/dist/mir/mksh/mksh-R59b.tgz"

SRC_URI[md5sum] = "dce6abffc2036288540b9ba11dfb2ec8"
SRC_URI[sha256sum] = "907ed1a9586e7f18bdefdd4a763aaa8397b755e15034aa54f4d753bfb272e0e6"

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
