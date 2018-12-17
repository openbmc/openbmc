SUMMARY = "A shared storage lock manager"
DESCRIPTION = "sanlock  is  a lock manager built on shared storage.  Hosts with access \
to the storage can perform locking.   An  application  running  on  the \
hosts  is  given  a small amount of space on the shared block device or \
file, and uses sanlock for its  own  application-specific  synchronization. \
Internally,  the  sanlock  daemon manages locks using two disk-based \
lease algorithms: delta leases and paxos leases."
HOMEPAGE = "https://pagure.io/sanlock"
SECTION = "utils"

LICENSE = "LGPLv2+ & GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://README.license;md5=60487bf0bf429d6b5aa72b6d37a0eb22"

SRC_URI = "git://pagure.io/sanlock.git;protocol=http \
           file://0001-fix-compile-failure-with-libc-musl.patch \
          "
SRCREV = "90b2ffa77edd46bea007b7bb39bfd4d2db2ff7af"
S = "${WORKDIR}/git"

DEPENDS = "libaio util-linux"

inherit distutils

do_configure[noexec] = "1"

do_compile_prepend () {
    oe_runmake -C wdmd CMD_LDFLAGS="${LDFLAGS}" LIB_LDFLAGS="${LDFLAGS}"
    oe_runmake -C src CMD_LDFLAGS="${LDFLAGS}" LIB_ENTIRE_LDFLAGS="${LDFLAGS}" LIB_CLIENT_LDFLAGS="${LDFLAGS}"
    cd ${S}/python
}

do_install_prepend () {
    oe_runmake -C wdmd DESTDIR=${D} LIBDIR=${libdir} install
    oe_runmake -C src DESTDIR=${D} LIBDIR=${libdir} install
    cd ${S}/python
}
