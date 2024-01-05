SUMMARY = "Pipe viewer test recipe for devtool upgrade test"
LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=9c50db2589ee3ef10a9b7b2e50ce1d02"

SRC_URI = "http://www.ivarch.com/programs/sources/pv-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "http://www.ivarch.com/programs/pv.shtml"
RECIPE_NO_UPDATE_REASON = "This recipe is used to test devtool upgrade feature"

SRC_URI[md5sum] = "9365d86bd884222b4bf1039b5a9ed1bd"
SRC_URI[sha1sum] = "63a0801350e812541c7f8e9ad74e0d6b629d0b39"
SRC_URI[sha256sum] = "681bcca9784bf3cb2207e68236d1f68e2aa7b80f999b5750dc77dcd756e81fbc"
SRC_URI[sha384sum] = "5fff6390465ff23dbf573fcf39dfad3aed2f92074a35e6c02abe58b7678858d90fa6572ff4cb56df8b3e217c739cdbe3"
SRC_URI[sha512sum] = "32efe7071a363f547afc74e96774f711795edda1d2702823a347d0f9953e859b7d8c45b3e63e18ffb9e0d5ed5910be652d7d727c8676e81b6cb3aed0b13aec00"

PR = "r5"

S = "${WORKDIR}/pv-${PV}"

EXCLUDE_FROM_WORLD = "1"

inherit autotools

