SUMMARY = "Advanced front-end for dpkg"
LICENSE = "GPLv2.0+"
SECTION = "base"

SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/apt/1.2.24/${BPN}_${PV}.tar.xz \
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
           "
SRC_URI[md5sum] = "ce8f9ab11f4fd0a08ec73eaffd75c8f0"
SRC_URI[sha256sum] = "fa1311a9ce00e72379a0a3bc6d240ba30c0968cfbbb3472859e50b99e24e9598"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/apt/"

inherit autotools gettext useradd

EXTRA_AUTORECONF = "--exclude=autopoint,autoheader"

do_configure_prepend() {
    rm -rf ${S}/buildlib/config.sub
    rm -rf ${S}/buildlib/config.guess
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir /nonexistent --shell /bin/false --user-group _apt"
