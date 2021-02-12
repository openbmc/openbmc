SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz"

SRC_URI[md5sum] = "5ee30bf7c76f00cc69f92388ad776e2a"
SRC_URI[sha256sum] = "8f1a09c070738adc695df903f2e7f74308dd8d991b914c0bc390a0e6021294dd"

S = "${WORKDIR}/IO-Tty-${PV}"

inherit cpan

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/IO/Tty/.debug/"

