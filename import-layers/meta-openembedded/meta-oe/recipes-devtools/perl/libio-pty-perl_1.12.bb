SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz"

SRC_URI[md5sum] = "11695a1a516b3bd1b90ce75ff0ce3e6d"
SRC_URI[sha256sum] = "a2ef8770d3309178203f8c8ac25e623e63cf76e97830fd3be280ade1a555290d"

S = "${WORKDIR}/IO-Tty-${PV}"

inherit cpan

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/IO/Tty/.debug/"

