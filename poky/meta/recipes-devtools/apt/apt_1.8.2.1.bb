SUMMARY = "Advanced front-end for dpkg"
LICENSE = "GPLv2.0+"
SECTION = "base"

# Triehash script taken from https://github.com/julian-klode/triehash
SRC_URI = "${DEBIAN_MIRROR}/main/a/apt/${BPN}_${PV}.tar.xz \
           file://triehash \
           file://0001-Disable-documentation-directory-altogether.patch \
           file://0001-Fix-musl-build.patch \
           file://0001-CMakeLists.txt-avoid-changing-install-paths-based-on.patch \
           file://0001-test-libapt-do-not-use-gtest-from-the-host.patch \
           "
     
SRC_URI_append_class-native = " \
           file://0001-Do-not-init-tables-from-dpkg-configuration.patch \
           file://0001-Revert-always-run-dpkg-configure-a-at-the-end-of-our.patch \
           file://0001-Do-not-configure-packages-on-installation.patch \
           "

SRC_URI_append_class-nativesdk = " \
           file://0001-Do-not-init-tables-from-dpkg-configuration.patch \
           file://0001-Revert-always-run-dpkg-configure-a-at-the-end-of-our.patch \
           file://0001-Do-not-configure-packages-on-installation.patch \
           "

SRC_URI[sha256sum] = "6d447f2e9437ec24e78350b63bb0592bee1f050811d51990b0c783183b0983f8"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/apt/"

inherit cmake perlnative bash-completion upstream-version-is-even useradd

# User is added to allow apt to drop privs, will runtime warn without
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /nonexistent --no-create-home _apt"

BBCLASSEXTEND = "native nativesdk"

DEPENDS += "virtual/libiconv virtual/libintl db gnutls lz4 zlib bzip2 xz"

EXTRA_OECMAKE_append = " -DCURRENT_VENDOR=debian -DWITH_DOC=False \
    -DUSE_NLS=False -DDPKG_DATADIR=${datadir}/dpkg \
    -DTRIEHASH_EXECUTABLE=${WORKDIR}/triehash \
    -DCMAKE_DISABLE_FIND_PACKAGE_Zstd=True \
"

do_configure_prepend () {
    echo "set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH )" >>  ${WORKDIR}/toolchain.cmake

}

# Unfortunately apt hardcodes this all over the place
FILES_${PN} += "${prefix}/lib/dpkg ${prefix}/lib/apt"
RDEPENDS_${PN} += "bash perl dpkg"

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

do_install_append_class-native() {
    customize_apt_conf_sample
}

do_install_append_class-nativesdk() {
    customize_apt_conf_sample
}


do_install_append_class-target() {
    #Write the correct apt-architecture to apt.conf
    APT_CONF=${D}/etc/apt/apt.conf
    echo 'APT::Architecture "${DPKG_ARCH}";' > ${APT_CONF}
}

# Avoid non-reproducible -src package
do_install_append () {
        sed -i -e "s,${B},,g" \
            ${B}/apt-pkg/tagfile-keys.cc
}
