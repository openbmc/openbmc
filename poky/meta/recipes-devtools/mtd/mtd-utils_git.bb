SUMMARY = "Tools for managing memory technology devices"
HOMEPAGE = "http://www.linux-mtd.infradead.org/"
DESCRIPTION = "mtd-utils tool is a generic Linux subsystem for memory devices, especially Flash devices."
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://include/common.h;beginline=1;endline=17;md5=ba05b07912a44ea2bf81ce409380049c"

inherit autotools pkgconfig update-alternatives

DEPENDS = "zlib e2fsprogs util-linux"
RDEPENDS:mtd-utils-tests += "bash"

PV = "2.2.0"

SRCREV = "31e990c56aba7584cde310685d663bb122f16003"
SRC_URI = "git://git.infradead.org/mtd-utils.git;branch=master"

S = "${WORKDIR}/git"

# xattr support creates an additional compile-time dependency on acl because
# the sys/acl.h header is needed. libacl is not needed and thus enabling xattr
# regardless whether acl is enabled or disabled in the distro should be okay.
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'xattr', d)} lzo jffs ubifs"
PACKAGECONFIG[lzo] = "--with-lzo,--without-lzo,lzo"
PACKAGECONFIG[xattr] = "--with-xattr,--without-xattr,acl"
PACKAGECONFIG[crypto] = "--with-crypto,--without-crypto,openssl"
PACKAGECONFIG[jffs] = "--with-jffs,--without-jffs"
PACKAGECONFIG[ubifs] = "--with-ubifs,--without-ubifs"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"

CPPFLAGS:append:riscv64  = " -pthread -D_REENTRANT"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} ${@bb.utils.contains('PACKAGECONFIG', 'xattr', '', '-DWITHOUT_XATTR', d)} -I${S}/include' 'BUILDDIR=${S}'"

# Use higher priority than corresponding BusyBox-provided applets
ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "flashcp flash_eraseall flash_lock flash_unlock nanddump nandwrite"
ALTERNATIVE:${PN}-ubifs = "ubiattach ubidetach ubimkvol ubirename ubirmvol ubirsvol ubiupdatevol"

ALTERNATIVE_LINK_NAME[nandwrite] = "${sbindir}/nandwrite"
ALTERNATIVE_LINK_NAME[nanddump] = "${sbindir}/nanddump"
ALTERNATIVE_LINK_NAME[ubiattach] = "${sbindir}/ubiattach"
ALTERNATIVE_LINK_NAME[ubidetach] = "${sbindir}/ubidetach"
ALTERNATIVE_LINK_NAME[ubimkvol] = "${sbindir}/ubimkvol"
ALTERNATIVE_LINK_NAME[ubirename] = "${sbindir}/ubirename"
ALTERNATIVE_LINK_NAME[ubirmvol] = "${sbindir}/ubirmvol"
ALTERNATIVE_LINK_NAME[ubirsvol] = "${sbindir}/ubirsvol"
ALTERNATIVE_LINK_NAME[ubiupdatevol] = "${sbindir}/ubiupdatevol"
ALTERNATIVE_LINK_NAME[flash_eraseall] = "${sbindir}/flash_eraseall"
ALTERNATIVE_LINK_NAME[flash_lock] = "${sbindir}/flash_lock"
ALTERNATIVE_LINK_NAME[flash_unlock] = "${sbindir}/flash_unlock"
ALTERNATIVE_LINK_NAME[flashcp] = "${sbindir}/flashcp"

do_install () {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
	install -d ${D}${includedir}/mtd
	install -d ${D}${libdir}
	install -m 0644 ${S}/include/libubi.h ${D}${includedir}
	install -m 0644 ${S}/include/libmtd.h ${D}${includedir}
	install -m 0644 ${S}/include/libscan.h ${D}${includedir}
	install -m 0644 ${S}/include/libubigen.h ${D}${includedir}
	oe_libinstall -a libubi ${D}${libdir}/
	oe_libinstall -a libmtd ${D}${libdir}/
}

PACKAGES =+ "mtd-utils-misc mtd-utils-tests"
PACKAGES =+ "${@bb.utils.contains("PACKAGECONFIG", "jffs", "mtd-utils-jffs2", "", d)}"
PACKAGES =+ "${@bb.utils.contains("PACKAGECONFIG", "ubifs", "mtd-utils-ubifs", "", d)}"

FILES:mtd-utils-jffs2 = "${sbindir}/mkfs.jffs2 ${sbindir}/jffs2dump ${sbindir}/jffs2reader ${sbindir}/sumtool"
FILES:mtd-utils-ubifs = "${sbindir}/mkfs.ubifs ${sbindir}/ubi*"
FILES:mtd-utils-misc = "${sbindir}/nftl* ${sbindir}/ftl* ${sbindir}/rfd* ${sbindir}/doc* ${sbindir}/serve_image ${sbindir}/recv_image"
FILES:mtd-utils-tests = "${libexecdir}/mtd-utils/*"

BBCLASSEXTEND = "native nativesdk"

# git/.compr.c.dep:46: warning: NUL character seen; rest of line ignored
# git/.compr.c.dep:47: *** missing separator.  Stop.
PARALLEL_MAKE = ""
