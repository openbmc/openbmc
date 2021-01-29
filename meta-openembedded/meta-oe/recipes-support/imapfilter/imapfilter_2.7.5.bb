SUMMARY = "IMAPFilter is a mail filtering utility that processes mailboxes based on IMAP queries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db3b99f230f9758fd77e4a0654e2266d"

SRC_URI = "https://codeload.github.com/lefcha/${BPN}/tar.gz/v${PV};downloadfilename=${BP}.tar.gz \
           file://ldflags.patch \
"
SRC_URI[sha256sum] = "ab19f840712e6951e51c29e44c43b3b2fa42e93693f98f8969cc763a4fad56bf"

DEPENDS= "openssl lua libpcre2"

EXTRA_OEMAKE_append = " PREFIX=${prefix}"

do_install(){
    oe_runmake DESTDIR=${D} install

    # No need for manuals at this point, MANDIR is hardcoded to depend on prefix
    rm -rf ${D}${prefix}/man
}

ASNEEDED = ""
