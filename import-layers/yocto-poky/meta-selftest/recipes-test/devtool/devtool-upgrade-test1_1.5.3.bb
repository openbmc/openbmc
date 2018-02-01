SUMMARY = "Pipe viewer test recipe for devtool upgrade test"
LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=9c50db2589ee3ef10a9b7b2e50ce1d02"

SRC_URI = "http://www.ivarch.com/programs/sources/pv-${PV}.tar.gz \
           file://0001-Add-a-note-line-to-the-quick-reference.patch"

SRC_URI[md5sum] = "9365d86bd884222b4bf1039b5a9ed1bd"
SRC_URI[sha256sum] = "681bcca9784bf3cb2207e68236d1f68e2aa7b80f999b5750dc77dcd756e81fbc"

PR = "r5"

S = "${WORKDIR}/pv-${PV}"

EXCLUDE_FROM_WORLD = "1"

inherit autotools

