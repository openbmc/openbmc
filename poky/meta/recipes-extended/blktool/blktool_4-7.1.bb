SUMMARY = "Tune low-level block device parameters"
DESCRIPTION = "blktool is used for querying and/or changing settings \
of a block device. It is like hdparm but a more general tool, as it \
works on SCSI, IDE and SATA devices."
HOMEPAGE = "http://packages.debian.org/unstable/admin/blktool"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://blktool.c;beginline=7;endline=8;md5=a5e798ea98fd50972088968a15e5f373"

DEPENDS = "glib-2.0"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/b/${BPN}/${BPN}_4.orig.tar.gz;name=tarball \
           file://0001-fix-typos-in-manpage.patch \
           file://0002-fix-string-error.patch \
           file://0003-Fix-3-d-argument-for-BLKROSET-it-must-be-const-int.patch \
           file://0004-fix-ftbfs-glibc-2.28.patch \
          "

SRC_URI[tarball.md5sum] = "62edc09c9908107e69391c87f4f3fd40"
SRC_URI[tarball.sha256sum] = "b1e6d5912546d2a4b704ec65c2b9664aa3b4663e7d800e06803330335a2cb764"

# for this package we're mostly interested in tracking debian patches,
# and not in the upstream version where all development has effectively stopped
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/b/blktool/"
UPSTREAM_CHECK_REGEX = "(?P<pver>((\d+\.*)+)-((\d+\.*)+))\.(diff|debian\.tar)\.(gz|xz)"

S = "${WORKDIR}/${BPN}-4.orig"

inherit autotools pkgconfig
