SUMMARY = "Helper application and MIME type associations for file types"
HOMEPAGE = "https://pagure.io/mailcap"
DESCRIPTION = "The mailcap file is used by the metamail program. Metamail reads the \
mailcap file to determine how it should display non-text or multimedia \
material. Basically, mailcap associates a particular type of file \
with a particular program that a mail agent or other program can call \
in order to handle the file. Mailcap should be installed to allow \
certain programs to be able to handle non-text files. \
\
Also included in this package is the mime.types file which contains a \
list of MIME types and their filename extension associations, used  \
by several applications e.g. to determine MIME types for filenames."

SECTION = "System Environment/Base"

LICENSE = "PD & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8dce08227d135cfda1f19d4c0c6689de"

SRC_URI = "https://releases.pagure.org/${BPN}/${BP}.tar.xz"

SRC_URI[md5sum] = "3d1a14a80bb85564c323562670f1c01f"
SRC_URI[sha256sum] = "e6f615ec2ffe705522075d960744534c443ece66acfb34fada38d567d8823639"

inherit update-alternatives

do_install() {
    oe_runmake install DESTDIR=${D} sysconfdir=${sysconfdir} mandir=${mandir}
}

ALTERNATIVE_${PN} = "mime.types"
ALTERNATIVE_LINK_NAME[mime.types] = "${sysconfdir}/mime.types"
