SUMMARY = "basic system security checks"
DESCRIPTION = "checksecurity is a simple package which will scan your system for several simple security holes."
SECTION = "security"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "http://ftp.de.debian.org/debian/pool/main/c/checksecurity/checksecurity_${PV}+nmu3.tar.gz \
           file://check-setuid-use-more-portable-find-args.patch \
          "

SRC_URI[sha256sum] = "12b043dc7b38512cdf0735c7c147a4f9e60d83a397b5b8ec130c65ceddbe1a0c"

S = "${UNPACKDIR}/checksecurity-${PV}+nmu3"

# allow for anylocal, no need to patch
LOGDIR = "/etc/checksecurity"

do_compile() {
    sed -i -e "s;LOGDIR=/var/log/setuid;LOGDIR=${LOGDIR};g" ${B}/etc/check-setuid.conf
    sed -i -e "s;LOGDIR=/var/log/setuid;LOGDIR=${LOGDIR};g" ${B}/plugins/check-setuid
    sed -i -e "s;LOGDIR:=/var/log/setuid;LOGDIR:=${LOGDIR};g" ${B}/plugins/check-setuid
}

do_install() {
    oe_runmake PREFIX=${D}
}

RDEPENDS:${PN} = "perl libenv-perl perl-module-tie-array perl-module-getopt-long perl-module-file-glob perl-module-carp perl-module-env perl-module-tap-parser-iterator-array util-linux findutils coreutils"
