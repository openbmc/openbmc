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

SRC_URI[sha256sum] = "c6f1dc3e75b7e2e5ceac4f857fbd2ee0ddb3f0169c2b39ea9187af34208e98de"

RDEPENDS_${PN} += "binutils vim squashfs-tools python3-libarchive-c python3-magic python3-rpm"

# Dependencies don't build for musl
COMPATIBLE_HOST_libc-musl = 'null'

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/diffoscope \
		MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc \
		RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
		RPM_ETCCONFIGDIR=${STAGING_DIR_NATIVE}
}

BBCLASSEXTEND = "native"
