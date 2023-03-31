SUMMARY = "A self-extracting archiving tool for Unix systems, in 100% shell script."
DESCRIPTION = "\
    makeself.sh is a small shell script that generates a self-extractable \
    compressed tar archive from a directory. The resulting file appears as \
    a shell script (many of those have a .run suffix), and can be launched as is.\
"
HOMEPAGE = "https://makeself.io/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "\
    git://github.com/megastep/${BPN}.git;protocol=https;branch=master \
"

SRCREV = "09488c50c6bdc40aec8e3a9b23a539c5054a634c"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/makeself.sh ${D}${bindir}/
    install -m 0755 ${S}/makeself-header.sh ${D}${bindir}/
}

BBCLASSEXTEND = "native"
