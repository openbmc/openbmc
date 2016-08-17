SUMMARY = "Helper application and MIME type associations for file types"
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

SRC_URI = "https://git.fedorahosted.org/cgit/${BPN}.git/snapshot/${BPN}-r2-1-45.tar.gz"
SRC_URI[md5sum] = "2320a77b2fc82078c9d6a59b29234bc8"
SRC_URI[sha256sum] = "a73e2f93625475014066f414873cb9f0a4b1189942d94fade9a03e59be3745b7"
LICENSE = "PD & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=100fcfb84512ccc03ffc7d89ac391305"
S = "${WORKDIR}/${BPN}-r2-1-45"
do_install() {
    oe_runmake install DESTDIR=${D} sysconfdir=${sysconfdir} mandir=${mandir}
}
