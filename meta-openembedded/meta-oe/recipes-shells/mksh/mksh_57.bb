DESCRIPTION = "The MirBSD Korn Shell - an enhanced version of the public domain ksh"
HOMEPAGE = "http://www.mirbsd.org/mksh.htm"
SECTION = "base/shell"

LICENSE = "${@bb.utils.contains("TCLIBC", "glibc", "MirOS & ISC", "MirOS", d)}"
LIC_FILES_CHKSUM = "file://main.c;beginline=6;endline=26;md5=0651e575e39d1a3e884562e25d491fc7 \
                    file://strlcpy.c;beginline=1;endline=17;md5=d953f28f0c43ee29e238ec9bc15df2a0 \
                   "

SRC_URI = "http://www.mirbsd.org/MirOS/dist/mir/mksh/mksh-R57.tgz"

SRC_URI[md5sum] = "4d2686535252ea6a57bdec531498239a"
SRC_URI[sha256sum] = "3d101154182d52ae54ef26e1360c95bc89c929d28859d378cc1c84f3439dbe75"

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
