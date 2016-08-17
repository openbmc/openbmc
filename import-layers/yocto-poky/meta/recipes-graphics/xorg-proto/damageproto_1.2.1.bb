require xorg-proto-common.inc

SUMMARY = "Xdamage: X Damage extension headers"

DESCRIPTION = "This package provides the wire protocol for the DAMAGE \
extension.  The DAMAGE extension allows applications to receive \
information about changes made to pixel contents of windows and \
pixmaps."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d5f5a2de65c3a84cbde769f07a769608 \
                    file://damagewire.h;endline=23;md5=4a4501a592dbc7de5ce89255e50d0296"

RCONFLICTS_${PN} = "damageext"
BBCLASSEXTEND = "native"
PR = "r1"
PE = "1"

SRC_URI[md5sum] = "998e5904764b82642cc63d97b4ba9e95"
SRC_URI[sha256sum] = "5c7c112e9b9ea8a9d5b019e5f17d481ae20f766cb7a4648360e7c1b46fc9fc5b"
