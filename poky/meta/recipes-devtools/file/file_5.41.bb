SUMMARY = "File classification tool"
DESCRIPTION = "File attempts to classify files depending \
on their contents and prints a description if a match is found."
HOMEPAGE = "http://www.darwinsys.com/file/"
SECTION = "console/utils"

# two clause BSD
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;beginline=2;md5=0251eaec1188b20d9a72c502ecfdda1b"

DEPENDS = "file-replacement-native"
DEPENDS:class-native = "bzip2-replacement-native"

SRC_URI = "git://github.com/file/file.git;branch=master;protocol=https"

SRCREV = "504206e53a89fd6eed71aeaf878aa3512418eab1"
S = "${WORKDIR}/git"

inherit autotools update-alternatives

PACKAGECONFIG ??= "bz2 lzma zlib"
PACKAGECONFIG[bz2] = "--enable-bzlib, --disable-bzlib, bzip2"
PACKAGECONFIG[lzma] = "--enable-xzlib, --disable-xzlib, xz"
PACKAGECONFIG[zlib] = "--enable-zlib, --disable-zlib, zlib"

EXTRA_OECONF += "--disable-libseccomp"

ALTERNATIVE:${PN} = "file"
ALTERNATIVE_LINK_NAME[file] = "${bindir}/file"

EXTRA_OEMAKE:append:class-target = " -e FILE_COMPILE=${STAGING_BINDIR_NATIVE}/file-native/file"
EXTRA_OEMAKE:append:class-nativesdk = " -e FILE_COMPILE=${STAGING_BINDIR_NATIVE}/file-native/file"

FILES:${PN} += "${datadir}/misc/*.mgc"

do_compile:append:class-native() {
	oe_runmake check
}

do_install:append:class-native() {
	create_cmdline_wrapper ${D}/${bindir}/file \
		--magic-file ${datadir}/misc/magic.mgc
}

do_install:append:class-nativesdk() {
	create_cmdline_wrapper ${D}/${bindir}/file \
		--magic-file ${datadir}/misc/magic.mgc
}

BBCLASSEXTEND = "native nativesdk"
PROVIDES:append:class-native = " file-replacement-native"
# Don't use NATIVE_PACKAGE_PATH_SUFFIX as that hides libmagic from anyone who
# depends on file-replacement-native.
bindir:append:class-native = "/file-native"
