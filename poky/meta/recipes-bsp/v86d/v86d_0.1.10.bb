SUMMARY = "User support binary for the uvesafb kernel module"
HOMEPAGE = "https://tracker.debian.org/pkg/v86d"
DESCRIPTION = "v86d provides a backend for kernel drivers that need to execute x86 BIOS code. The code is executed in a controlled environment and the results are passed back to the kernel via the netlink interface."

# the copyright info is at the bottom of README, expect break
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README;md5=94ac1971e4f2309dc322d598e7b1f7dd"

RRECOMMENDS:${PN} = "kernel-module-uvesafb"

SRC_URI = "http://snapshot.debian.org/archive/debian/20110427T035506Z/pool/main/v/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://Update-x86emu-from-X.org.patch \
           file://ar-from-env.patch \
           file://Support-for-cross-compilation.patch \
"

SRC_URI[md5sum] = "889686ec8424468fe0d205742e77a4c2"
SRC_URI[sha256sum] = "93575c82e4307d8c4c370ec6b767f5cf87e527b2378146d652a6d8e25d5bdbc5"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

do_configure () {
	TARGET_ARCH="${TARGET_ARCH}" ./configure --with-x86emu
}

do_compile () {
	KDIR="${STAGING_DIR_HOST}/usr" make
}

do_install () {
	install -d ${D}${base_sbindir}
	install v86d ${D}${base_sbindir}/
}
