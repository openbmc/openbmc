SUMMARY = "in-depth comparison of files, archives, and directories"
DESCRIPTION = "Tries to get to the bottom of what makes files or directories \
different. It will recursively unpack archives of many kinds and transform \
various binary formats into more human-readable form to compare them. \
It can compare two tarballs, ISO images, or PDF just as easily."
HOMEPAGE = "https://diffoscope.org/"
BUGTRACKER = "https://salsa.debian.org/reproducible-builds/diffoscope/-/issues"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit pypi setuptools3

SRC_URI[sha256sum] = "daf539d33140ecd4c5dcf18c894bbc09aeab5307b44de28b12ca34434d47c5ba"

RDEPENDS:${PN} += "\
        binutils \
        python3-curses \
        python3-difflib \
        python3-fcntl \
        python3-json \
        python3-libarchive-c \
        python3-magic \
        python3-multiprocessing \
        python3-pprint \
        python3-rpm \
        squashfs-tools \
        vim \
        "

do_install:append:class-native() {
	create_wrapper ${D}${bindir}/diffoscope \
		MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc \
		RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
		LD_LIBRARY_PATH=${STAGING_LIBDIR_NATIVE} \
		RPM_ETCCONFIGDIR=${STAGING_DIR_NATIVE}
}

BBCLASSEXTEND = "native"
