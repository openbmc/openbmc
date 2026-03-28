SUMMARY = "IMAPFilter is a mail filtering utility that processes mailboxes based on IMAP queries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c9e8d74e78283c6319317d3cb15eded4"

# v2.7.6
SRCREV = "23b693f7f7cad8b459beb5cf748078f9cc0e5dc8"
SRC_URI = "git://github.com/lefcha/imapfilter;protocol=https;branch=master;tag=v${PV} \
           file://ldflags.patch \
"

DEPENDS = "openssl lua libpcre2"

EXTRA_OEMAKE:append = " PREFIX=${prefix}"

do_install(){
    oe_runmake DESTDIR=${D} install

    # No need for manuals at this point, MANDIR is hardcoded to depend on prefix
    rm -rf ${D}${prefix}/man
}

ASNEEDED = ""
