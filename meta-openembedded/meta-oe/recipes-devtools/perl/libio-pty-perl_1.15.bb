SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz"

SRC_URI[md5sum] = "060103c6d6e4d9833fa7715860f2923b"
SRC_URI[sha256sum] = "43f9cc0f87620bbb159e0890e196b23a8e6419cbd04224c10f3dcee948f6b51a"

S = "${WORKDIR}/IO-Tty-${PV}"

inherit cpan

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/IO/Tty/.debug/"

