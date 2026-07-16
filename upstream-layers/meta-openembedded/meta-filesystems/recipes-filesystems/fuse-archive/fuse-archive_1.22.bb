SUMMARY = "FUSE filesystem that serves archives (zip, tar, 7z, iso, ...) read-only"
DESCRIPTION = "Mounts an archive file (e.g. foo.zip, foo.tar, foo.7z) as a \
read-only FUSE file system, without extracting it to disk first."
HOMEPAGE = "https://github.com/google/fuse-archive"
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/fuse-archive.git;branch=main;protocol=https;tag=v${PV}"
SRCREV = "e218685189aabebb95d33e2542e2a122ea81392d"

DEPENDS = "boost fuse3 libarchive"

inherit pkgconfig

do_install() {
	oe_runmake install 'DESTDIR=${D}'
}
