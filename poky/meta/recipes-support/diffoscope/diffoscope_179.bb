SUMMARY = "in-depth comparison of files, archives, and directories"
DESCRIPTION = "Tries to get to the bottom of what makes files or directories \
different. It will recursively unpack archives of many kinds and transform \
various binary formats into more human-readable form to compare them. \
It can compare two tarballs, ISO images, or PDF just as easily."
HOMEPAGE = "https://diffoscope.org/"
BUGTRACKER = "https://salsa.debian.org/reproducible-builds/diffoscope/-/issues"
LICENSE = "GPL-3.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PYPI_PACKAGE = "diffoscope"

inherit pypi setuptools3

SRC_URI[sha256sum] = "17790d463b757b6d05e0734db06ba5e79ac63fb64bf9b834dc7440477582a38a"

RDEPENDS:${PN} += "binutils vim squashfs-tools python3-libarchive-c python3-magic python3-rpm"

# Dependencies don't build for musl
COMPATIBLE_HOST:libc-musl = 'null'

do_install:append:class-native() {
	create_wrapper ${D}${bindir}/diffoscope \
		MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc \
		RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
		LD_LIBRARY_PATH=${STAGING_LIBDIR_NATIVE} \
		RPM_ETCCONFIGDIR=${STAGING_DIR_NATIVE}
}

BBCLASSEXTEND = "native"
