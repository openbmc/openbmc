# This include file contains global definitions for the
# various NIS packages.
#
# These packages will only function correctly with glibc -
# the rpcsvc functionality is not present in uclibc
DESCRIPTION = "NIS Server and Tools"
HOMEPAGE = "http://www.linux-nis.org/nis/"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit autotools gettext pkgconfig

# install is broken because src/Makefile heroically adds '-s'
# to the install flags - passing -s to the build /usr/bin/install!
# install-strip gets it right but installs ypbind -m <default>,
# not -m 555.  In an OE build this is not, so far as I can see,
# a security problem (and this fix to the build problem is *much*
# easier and more maintainable.)
do_install() {
    oe_runmake 'DESTDIR=${D}' install-strip
}
