SUMMARY = "Perl extension for converting Pod to old-style Pod"
DESCRIPTION = "Pod::Plainer uses Pod::Parser which takes Pod with the (new) 'C<< .. >>' \
constructs and returns the old(er) style with just 'C<>'; '<' and '>' are replaced by \
'E<lt>' and 'E<gt>'. \
\
This can be used to pre-process Pod before using tools which do not recognise the new style Pods."

HOMEPAGE = "http://search.cpan.org/dist/Pod-Plainer/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=27;md5=227cf83970fc61264845825d9d2bf6f8"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/R/RM/RMBARKER/Pod-Plainer-${PV}.tar.gz"

SRC_URI[md5sum] = "f502eacd1a40894b9dfea55fc2cd5e7d"
SRC_URI[sha256sum] = "1bbfbf7d1d4871e5a83bab2137e22d089078206815190eb1d5c1260a3499456f"

S = "${WORKDIR}/Pod-Plainer-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
