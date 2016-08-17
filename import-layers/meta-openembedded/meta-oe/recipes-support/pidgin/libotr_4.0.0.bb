SUMMARY = "(OTR) Messaging allows you to have private conversations over instant messaging"
HOMEPAGE = "http://www.cypherpunks.ca/otr/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=92fe174bad6da3763f6e9e9eaff6df24"
DEPENDS = "libgcrypt libgpg-error"

SRC_URI = "http://www.cypherpunks.ca/otr/${BP}.tar.gz \
           file://fix_qa-issue_include.patch \
           file://sepbuild.patch \
"

SRC_URI[md5sum] = "00979dca82d70383fcd1b01f3974363c"
SRC_URI[sha256sum] = "3f911994409898e74527730745ef35ed75c352c695a1822a677a34b2cf0293b4"

inherit autotools pkgconfig
