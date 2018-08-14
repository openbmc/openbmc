require xorg-proto-common.inc

SUMMARY = "Xcomposite: X composite extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
composite extension.  The X composite extension provides three related \
mechanisms for compositing and off-screen storage."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=2c4bfe136f4a4418ea2f2a96b7c8f3c5 \
                    file://composite.h;endline=43;md5=cbd44d4079053aa75930ed2f02b92926"

RCONFLICTS_${PN} = "compositeext"
BBCLASSEXTEND = "native"

PR = "r1"
PE = "1"

SRC_URI[md5sum] = "98482f65ba1e74a08bf5b056a4031ef0"
SRC_URI[sha256sum] = "049359f0be0b2b984a8149c966dd04e8c58e6eade2a4a309cf1126635ccd0cfc"

