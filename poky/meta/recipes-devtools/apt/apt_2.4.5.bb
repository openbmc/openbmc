SUMMARY = "Advanced front-end for dpkg"
DESCRIPTION = "APT is the Advanced Package Tool, an advanced interface to the Debian packaging system which provides the apt-get program."
HOMEPAGE = "https://packages.debian.org/jessie/apt"
LICENSE = "GPL-2.0-or-later"
SECTION = "base"

# Triehash script taken from https://github.com/julian-klode/triehash
SRC_URI = "${DEBIAN_MIRROR}/main/a/apt/${BPN}_${PV}.tar.xz \
           file://triehash \
           file://0001-Disable-documentation-directory-altogether.patch \
           file://0001-Fix-musl-build.patch \
           file://0001-CMakeLists.txt-avoid-changing-install-paths-based-on.patch \
           file://0001-cmake-Do-not-build-po-files.patch \
           file://0001-Hide-fstatat64-and-prlimit64-defines-on-musl.patch \
           file://0001-aptwebserver.cc-Include-array.patch \
           "

SRC_URI:append:class-native = " \
           file://0001-Do-not-init-tables-from-dpkg-configuration.patch \
           file://0001-Revert-always-run-dpkg-configure-a-at-the-end-of-our.patch \
           "

SRC_URI:append:class-nativesdk = " \
           file://0001-Do-not-init-tables-from-dpkg-configuration.patch \
           file://0001-Revert-always-run-dpkg-configure-a-at-the-end-of-our.patch \
           "

SRC_URI[sha256sum] = "5552f175c3a3924f5cda0c079b821b30f68a2521959f2c30ab164d2ec7993ecf"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/apt/"

inherit cmake perlnative bash-completion upstream-version-is-even useradd

# User is added to allow apt to drop privs, will runtime warn without
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home /nonexistent --no-create-home _apt"

BBCLASSEXTEND = "native nativesdk"

DEPENDS += "db gnutls lz4 zlib bzip2 xz libgcrypt xxhash"

EXTRA_OECMAKE:append = " -DCURRENT_VENDOR=debian -DWITH_DOC=False \
    -DDPKG_DATADIR=${datadir}/dpkg \
    -DTRIEHASH_EXECUTABLE=${WORKDIR}/triehash \
    -DCMAKE_DISABLE_FIND_PACKAGE_ZSTD=True \
    -DCMAKE_DISABLE_FIND_PACKAGE_SECCOMP=True \
    -DWITH_TESTS=False \
"

do_configure:prepend() {
	echo "set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH )" >>  ${WORKDIR}/toolchain.cmake
}

# Unfortunately apt hardcodes this all over the place
FILES:${PN} += "${prefix}/lib/dpkg ${prefix}/lib/apt"
RDEPENDS:${PN} += "bash perl dpkg"

customize_apt_conf_sample() {
	cat > ${D}${sysconfdir}/apt/apt.conf.sample << EOF
Dir "${STAGING_DIR_NATIVE}/"
{
   State "var/lib/apt/"
   {
      Lists "#APTCONF#/lists/";
      status "#ROOTFS#/var/lib/dpkg/status";
   };
   Cache "var/cache/apt/"
   {
      Archives "archives/";
      pkgcache "";
      srcpkgcache "";
   };
   Bin "${STAGING_BINDIR_NATIVE}/"
   {
      methods "${STAGING_LIBDIR}/apt/methods/";
      gzip "/bin/gzip";
      dpkg "dpkg";
      dpkg-source "dpkg-source";
      dpkg-buildpackage "dpkg-buildpackage";
      apt-get "apt-get";
      apt-cache "apt-cache";
   };
   Etc "#APTCONF#"
   {
      Preferences "preferences";
   };
   Log "var/log/apt";
};

APT
{
  Install-Recommends "true";
  Immediate-Configure "false";
  Architecture "i586";
  Get
  {
     Assume-Yes "true";
  };
};

Acquire
{
  AllowInsecureRepositories "true";
};

DPkg::Options {"--root=#ROOTFS#";"--admindir=#ROOTFS#/var/lib/dpkg";"--force-all";"--no-debsig"};
DPkg::Path "";
EOF
}

do_install:append:class-native() {
	customize_apt_conf_sample
}

do_install:append:class-nativesdk() {
	customize_apt_conf_sample
}

do_install:append:class-target() {
	# Write the correct apt-architecture to apt.conf
	APT_CONF=${D}${sysconfdir}/apt/apt.conf
	echo 'APT::Architecture "${DPKG_ARCH}";' > ${APT_CONF}

	# Remove /var/log/apt. /var/log is normally a link to /var/volatile/log
	# and /var/volatile is a tmpfs mount. So anything created in /var/log
	# will not be available when the tmpfs is mounted.
	rm -rf ${D}${localstatedir}/log
}

do_install:append() {
	# Avoid non-reproducible -src package
	sed -i -e "s,${B}/include/,,g" ${B}/apt-pkg/tagfile-keys.cc
}
