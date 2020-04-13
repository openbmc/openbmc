SUMMARY = "Advanced front-end for dpkg"
DESCRIPTION = "Provides command-line tools for searching and managing as well \
as querying information about packages as a low-level access to all features \
of the libapt-pkg library."
LICENSE = "GPLv2.0+"
SECTION = "base"

SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/${BPN}/${PV}/${BPN}_${PV}.tar.xz \
           file://use-host.patch \
           file://makerace.patch \
           file://no-nls-dpkg.patch \
           file://fix-gcc-4.6-null-not-defined.patch \
           file://truncate-filename.patch \
           file://nodoc.patch \
           file://disable-configure-in-makefile.patch \
           file://disable-test.patch \
           file://0001-environment.mak-musl-based-systems-can-generate-shar.patch \
           file://0001-apt-1.2.12-Fix-musl-build.patch \
           file://0001-Include-array.h-for-std-array.patch \
           "
SRC_URI[md5sum] = "d30eed9304e82ea8238c854b5c5a34d9"
SRC_URI[sha256sum] = "03ded4f5e9b8d43ecec083704b2dcabf20c182ed382db9ac7251da0b0b038059"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/apt/"

inherit autotools gettext useradd upstream-version-is-even

EXTRA_AUTORECONF = "--exclude=autopoint,autoheader"

do_configure_prepend() {
    rm -rf ${S}/buildlib/config.sub
    rm -rf ${S}/buildlib/config.guess
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir /nonexistent --shell /bin/false --user-group _apt"
