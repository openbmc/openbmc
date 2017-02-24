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

SRC_URI = "https://git.fedorahosted.org/cgit/${BPN}.git/snapshot/${BPN}-r2-1-46.tar.gz"
SRC_URI[md5sum] = "eee03824bf86480dc1db20be4f78237f"
SRC_URI[sha256sum] = "309059163fa3ef368f8a43fc38f7a45d9345fd725970d5b437ba175a0ee7ebc9"
LICENSE = "PD & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=100fcfb84512ccc03ffc7d89ac391305"
S = "${WORKDIR}/${BPN}-r2-1-46"
do_install() {
    oe_runmake install DESTDIR=${D} sysconfdir=${sysconfdir} mandir=${mandir}
}
