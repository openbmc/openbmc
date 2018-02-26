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
LIC_FILES_CHKSUM = "file://COPYING;md5=100fcfb84512ccc03ffc7d89ac391305"

SRC_URI = "https://releases.pagure.org/${BPN}/${BP}.tar.xz"

SRC_URI[md5sum] = "2c26e18e912a5cf00318fcf7f8f2d747"
SRC_URI[sha256sum] = "d7b023b237d6053bf05ff6786e0663c55c614efcc99cdf856120be13b5c29157"

do_install() {
    oe_runmake install DESTDIR=${D} sysconfdir=${sysconfdir} mandir=${mandir}
}
