SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
DESCRIPTION = "rsync is an open source utility that provides fast incremental file transfer."
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"
# GPL-2.0-or-later (<< 3.0.0), GPL-3.0-or-later (>= 3.0.0)
# Includes opennsh and xxhash dynamic link exception
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=24423708fe159c9d12be1ea29fcb18c7"

DEPENDS = "popt"

SRC_URI = "https://download.samba.org/pub/${BPN}/src/${BP}.tar.gz \
           file://rsyncd.conf \
           file://makefile-no-rebuild.patch \
           file://determism.patch \
           file://0001-Add-missing-prototypes-to-function-declarations.patch \
           "
SRC_URI[sha256sum] = "2924bcb3a1ed8b551fc101f740b9f0fe0a202b115027647cf69850d65fd88c52"

# Doesn't use automake
inherit autotools-brokensep

PACKAGECONFIG ??= "acl attr \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"

PACKAGECONFIG[acl] = "--enable-acl-support,--disable-acl-support,acl,"
PACKAGECONFIG[attr] = "--enable-xattr-support,--disable-xattr-support,attr,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[xxhash] = "--enable-xxhash,--disable-xxhash,xxhash"
PACKAGECONFIG[zstd] = "--enable-zstd,--disable-zstd,zstd"

# By default, if crosscompiling, rsync disables a number of
# capabilities, hardlinking symlinks and special files (i.e. devices)
CACHED_CONFIGUREVARS += "rsync_cv_can_hardlink_special=yes rsync_cv_can_hardlink_symlink=yes"

EXTRA_OEMAKE = 'STRIP=""'
EXTRA_OECONF = "--disable-md2man --with-nobody-group=nogroup"

#| ./simd-checksum-x86_64.cpp: In function 'uint32_t get_checksum1_cpp(char*, int32_t)':
#| ./simd-checksum-x86_64.cpp:89:52: error: multiversioning needs 'ifunc' which is not supported on this target
#|    89 | __attribute__ ((target("default"))) MVSTATIC int32 get_checksum1_avx2_64(schar* buf, int32 len, int32 i, uint32* ps1, uint32* ps2) { return i; }
#|       |                                                    ^~~~~~~~~~~~~~~~~~~~~
#| ./simd-checksum-x86_64.cpp:480:1: error: use of multiversioned function without a default
#|   480 | }
#|       | ^
#| If you can't fix the issue, re-run ./configure with --disable-roll-simd.
EXTRA_OECONF:append:libc-musl = " --disable-roll-simd"

# rsync uses configure.sh instead of configure, so delete that file
# to avoid confusion as we will generate configure.
do_configure:prepend () {
       rm -f ${S}/configure.sh
}

do_install:append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${UNPACKDIR}/rsyncd.conf ${D}${sysconfdir}
}

BBCLASSEXTEND = "native nativesdk"
