SUMMARY = "New set of tools for working with SquashFS images"
SECTION = "base"
LICENSE = "GPL-3.0+ & LGPL-3.0+ & MIT & BSD-2-Clause & Zlib"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=c0de2c0aca56349dab98e97992316f7e \
                    file://licenses/GPLv3.txt;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://licenses/hash_table.txt;md5=874823605326caeaabaa95bfbd0f9fb0 \
                    file://licenses/LGPLv3.txt;md5=3000208d539ec061b899bce1d9ce9404 \
                    file://licenses/LZ4.txt;md5=ebc2ea4814a64de7708f1571904b32cc \
                    file://licenses/xxhash.txt;md5=f042a9be092bd6d7fe6f217d8d00f4ca \
                    file://licenses/xz.txt;md5=1c389b9610ccfdb25f7abaea6a0bb5a4 \
                    file://licenses/zlib.txt;md5=ae27c72096606131f760e5f59cf98b06 \
                    file://licenses/zstd.txt;md5=8df8137b630239cbdd4c0674124cb0c8"

SRCREV = "b96f0fc154feef531be76034bf6e38925636146f"
SRC_URI = "git://github.com/AgentD/squashfs-tools-ng.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ??= "gzip xz lzo lz4 zstd ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[gzip] = "--with-gzip,--without-gzip,zlib"
PACKAGECONFIG[xz] = "--with-xz,--without-xz,xz"
PACKAGECONFIG[lzo] = "--with-lzo,--without-lzo,lzo"
PACKAGECONFIG[lz4] = "--with-lz4,--without-lz4,lz4"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

PACKAGES =+ "libsquashfs"
FILES:libsquashfs = "${libdir}/libsquashfs*${SOLIBS}"

BBCLASSEXTEND = "native nativesdk"
