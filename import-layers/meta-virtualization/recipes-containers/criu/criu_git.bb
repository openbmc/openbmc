SUMMARY = "CRIU"
DESCRIPTION = "Checkpoint/Restore In Userspace, or CRIU, is a software tool for \
Linux operating system. Using this tool, you can freeze a running application \
(or part of it) and checkpoint it to a hard drive as a collection of files. \
You can then use the files to restore and run the application from the point \
it was frozen at. The distinctive feature of the CRIU project is that it is \
mainly implemented in user space"
HOMEPAGE = "http://criu.org"
SECTION = "console/tools"
LICENSE = "GPLv2"

EXCLUDE_FROM_WORLD = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=5cc804625b8b491b6b4312f0c9cb5efa"

SRCREV = "4c5b23e52c1dc4e3fbbc7472b92e7b1ce9d22f02"
PR = "r0"
PV = "1.6+git${SRCPV}"

SRC_URI = "git://github.com/xemul/criu.git;protocol=git \
	   file://0001-criu-Fix-toolchain-hardcode.patch \
	   file://0002-criu-Skip-documentation-install.patch \
       file://0001-criu-Change-libraries-install-directory.patch \
	  "

COMPATIBLE_HOST = "(x86_64|arm|aarch64).*-linux"

DEPENDS += "protobuf-c-native protobuf-c"

S = "${WORKDIR}/git"

#
# CRIU just can be built on ARMv7 and ARMv6, so the Makefile check
# if the ARCH is ARMv7 or ARMv6.
# ARM BSPs need set CRIU_BUILD_ARCH variable for building CRIU.
#
EXTRA_OEMAKE_arm += "ARCH=${CRIU_BUILD_ARCH} WERROR=0"
EXTRA_OEMAKE_x86-64 += "ARCH=${TARGET_ARCH} WERROR=0"
EXTRA_OEMAKE_aarch64 += "ARCH=${TARGET_ARCH} WERROR=0"

EXTRA_OEMAKE_append += "SBINDIR=${sbindir} LIBDIR=${libdir} INCLUDEDIR=${includedir} PIEGEN=no"
EXTRA_OEMAKE_append += "LOGROTATEDIR=${sysconfdir} SYSTEMDUNITDIR=${systemd_unitdir}"

CFLAGS += "-D__USE_GNU -D_GNU_SOURCE"

# overide LDFLAGS to allow criu to build without: "x86_64-poky-linux-ld: unrecognized option '-Wl,-O1'"
export LDFLAGS=""

export BUILD_SYS
export HOST_SYS

inherit setuptools

do_compile_prepend() {
    rm -rf ${S}/protobuf/google/protobuf/descriptor.proto
    ln -s  ${PKG_CONFIG_SYSROOT_DIR}/usr/include/google/protobuf/descriptor.proto ${S}/protobuf/google/protobuf/descriptor.proto
}

do_compile () {
	oe_runmake
}

do_install () {
    oe_runmake PREFIX=${exec_prefix} LIBDIR=${libdir} DESTDIR="${D}" install
}

FILES_${PN} += "${systemd_unitdir}/ \
            ${libdir}/python2.7/site-packages/ \
            ${libdir}/pycriu/ \
            ${libdir}/crit-0.0.1-py2.7.egg-info \
            "
