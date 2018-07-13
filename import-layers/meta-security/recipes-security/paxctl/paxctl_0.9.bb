DESCRIPTION = "paxctl  is  a tool that allows PaX flags to be modified on a \
               per-binary basis. PaX is part of common  security-enhancing  \
               kernel  patches  and secure distributions, such as \
               GrSecurity or Adamantix and Hardened Gen-too, respectively."
HOMEPAGE = "https://pax.grsecurity.net/"	       
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://paxctl.c;beginline=1;endline=5;md5=0ddd065c61020dda79729e6bedaed2c7 \
                    file://paxctl-elf.c;beginline=1;endline=5;md5=99f453ce7f6d1687ee808982e2924813 \
		   "

SRC_URI = "http://pax.grsecurity.net/${BP}.tar.gz"

SRC_URI[md5sum] = "9bea59b1987dc4e16c2d22d745374e64"
SRC_URI[sha256sum] = "a330ddd812688169802a3ba29e5e3b19956376b8f6f73b8d7e9586eb04423c2e"

EXTRA_OEMAKE = "CC='${CC}' DESTDIR='${D}'"

do_install() {
	oe_runmake install
}

# The install target in the Makefile will fail for paxctl-native with error:
#   install -D --owner 0 --group 0 --mode a=rx paxctl .../sbin/paxctl
#   install: cannot change ownership of '.../sbin/paxctl': \
#   Operation not permitted
# Drop '--owner 0 --group 0' to fix the issue.
do_install_class-native() {
	local PROG=paxctl
	install -d ${D}${base_sbindir}
	install -d ${D}${mandir}/man1
	install --mode a=rx $PROG ${D}${base_sbindir}/$PROG
	install --mode a=r $PROG.1 ${D}${mandir}/man1/$PROG.1
}

# Avoid QA Issue: No GNU_HASH in the elf binary
INSANE_SKIP_${PN} = "ldflags" 

BBCLASSEXTEND = "native"
