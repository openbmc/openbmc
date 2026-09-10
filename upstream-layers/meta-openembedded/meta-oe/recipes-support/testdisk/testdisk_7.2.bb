DESCRIPTION = "TestDisk is a free data recovery software primarily designed to help recover lost partitions and/or make non-booting disks bootable again when these symptoms are caused by faulty software, certain types of viruses or human error (such as accidentally deleting your Partition Table)."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "jpeg ncurses e2fsprogs"

SRC_URI = "https://www.cgsecurity.org/${BP}.tar.bz2"
SRC_URI[sha256sum] = "f8343be20cb4001c5d91a2e3bcd918398f00ae6d8310894a5a9f2feb813c283f"

# The tarball sits at the site root which is not a browsable listing; the
# download wiki page is where releases are announced
UPSTREAM_CHECK_URI = "https://www.cgsecurity.org/wiki/TestDisk_Download"
UPSTREAM_CHECK_REGEX = "testdisk-(?P<pver>\d+(\.\d+)+)\.tar\.bz2"

inherit autotools pkgconfig
COMPATIBLE_HOST:libc-musl = "null"

PACKAGES =+ "${PN}-photorec"

DESCRIPTION:${PN}-photorec = "Photorec is file data recovery software designed to recover lost files including video, documents and archives from Hard Disks and CDRom and lost pictures (Photo Recovery) from digital camera memory."
FILES:${PN}-photorec = "${sbindir}/photorec"

