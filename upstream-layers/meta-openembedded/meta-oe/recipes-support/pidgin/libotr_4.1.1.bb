SUMMARY = "(OTR) Messaging allows you to have private conversations over instant messaging"
HOMEPAGE = "http://www.cypherpunks.ca/otr/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=92fe174bad6da3763f6e9e9eaff6df24"
DEPENDS = "libgcrypt libgpg-error"

SRC_URI = "http://www.cypherpunks.ca/otr/${BP}.tar.gz \
           file://fix_qa-issue_include.patch \
           file://sepbuild.patch \
           file://0001-tests-Include-missing-sys-socket.h-header.patch \
"

SRC_URI[sha256sum] = "8b3b182424251067a952fb4e6c7b95a21e644fbb27fbd5f8af2b2ed87ca419f5"

inherit autotools pkgconfig
