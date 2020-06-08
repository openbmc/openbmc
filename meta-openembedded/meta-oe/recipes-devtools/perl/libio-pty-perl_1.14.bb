SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz"

SRC_URI[md5sum] = "70bcec4b1b19838ed209fb96a13f3e89"
SRC_URI[sha256sum] = "51f3e4e311128bdb2c6a15f02c51376cb852ccf9df9bebe8dfbb5f9561eb95b5"

S = "${WORKDIR}/IO-Tty-${PV}"

inherit cpan

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/IO/Tty/.debug/"

