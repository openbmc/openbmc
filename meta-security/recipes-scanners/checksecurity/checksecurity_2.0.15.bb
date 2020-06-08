SUMMARY = "basic system security checks"
DESCRIPTION = "checksecurity is a simple package which will scan your system for several simple security holes."
SECTION = "security"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "http://ftp.de.debian.org/debian/pool/main/c/checksecurity/checksecurity_${PV}.tar.gz \
           file://setuid-log-folder.patch \
           file://check-setuid-use-more-portable-find-args.patch"

SRC_URI[md5sum] = "a30161c3e24d3be710b2fd13fcd1f32f"
SRC_URI[sha256sum] = "67abe3d6391c96146e96f376d3fd6eb7a9418b0f7fe205b465219889791dba32"

do_compile() {
}

do_install() {
    oe_runmake PREFIX=${D}
}

RDEPENDS_${PN} = "perl libenv-perl perl-module-tie-array perl-module-getopt-long perl-module-file-glob perl-module-carp perl-module-env perl-module-tap-parser-iterator-array util-linux findutils coreutils"
