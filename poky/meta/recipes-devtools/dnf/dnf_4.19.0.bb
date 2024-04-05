SUMMARY = "Package manager forked from Yum, using libsolv as a dependency resolver"
DESCRIPTION = "Software package manager that installs, updates, and removes \
packages on RPM-based Linux distributions. It automatically computes \
dependencies and determines the actions required to install packages."
HOMEPAGE = "https://github.com/rpm-software-management/dnf"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://PACKAGE-LICENSING;md5=4a0548e303dbc77f067335b4d688e745 \
                    "

SRC_URI = "git://github.com/rpm-software-management/dnf.git;branch=master;protocol=https \
           file://0001-Corretly-install-tmpfiles.d-configuration.patch \
           file://0001-Do-not-hardcode-etc-and-systemd-unit-directories.patch \
           file://0005-Do-not-prepend-installroot-to-logdir.patch \
           file://0029-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0030-Run-python-scripts-using-env.patch \
           file://0001-set-python-path-for-completion_helper.patch \
           file://0001-lock.py-fix-Exception-handling.patch \
           "

SRC_URI:append:class-native = "file://0001-dnf-write-the-log-lock-to-root.patch"

SRCREV = "566a61f9d8a2830ac6dcc3a94c59224cef1c3d03"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake gettext bash-completion setuptools3-base systemd

DEPENDS += "libdnf librepo libcomps python3-iniparse"

# manpages generation requires http://www.sphinx-doc.org/
EXTRA_OECMAKE = " -DWITH_MAN=0 -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
  python3-core \
  python3-codecs \
  python3-netclient \
  python3-email \
  python3-threading \
  python3-logging \
  python3-fcntl \
  librepo \
  python3-shell \
  libcomps \
  libdnf \
  python3-sqlite3 \
  python3-compression \
  python3-rpm \
  python3-iniparse \
  python3-json \
  python3-curses \
  python3-misc \
  "

RDEPENDS:${PN}:class-native = ""

RRECOMMENDS:${PN}:class-target += "gnupg"

# Create a symlink called 'dnf' as 'make install' does not do it, but
# .spec file in dnf source tree does (and then Fedora and dnf documentation
# says that dnf binary is plain 'dnf').
do_install:append() {
        ln -rs ${D}/${bindir}/dnf-3 ${D}/${bindir}/dnf
        ln -rs ${D}/${bindir}/dnf-automatic-3 ${D}/${bindir}/dnf-automatic
}

# Direct dnf-native to read rpm configuration from our sysroot, not the one it was compiled in
do_install:append:class-native() {
        create_wrapper ${D}/${bindir}/dnf \
                RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
                RPM_NO_CHROOT_FOR_SCRIPTS=1
}

do_install:append:class-nativesdk() {
        create_wrapper ${D}/${bindir}/dnf \
                RPM_CONFIGDIR=${SDKPATHNATIVE}${libdir_nativesdk}/rpm \
                RPM_NO_CHROOT_FOR_SCRIPTS=1
}

SYSTEMD_SERVICE:${PN} = "dnf-makecache.service dnf-makecache.timer \
                         dnf-automatic.service dnf-automatic.timer \
                         dnf-automatic-download.service dnf-automatic-download.timer \
                         dnf-automatic-install.service dnf-automatic-install.timer \
                         dnf-automatic-notifyonly.service dnf-automatic-notifyonly.timer \
"
SYSTEMD_AUTO_ENABLE ?= "disable"

SKIP_RECIPE[dnf] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'does not build without package_rpm in PACKAGE_CLASSES due disabled rpm support in libsolv', d)}"

# Packages for testing purposes
PACKAGES += "${PN}-test-main ${PN}-test-dep"
ALLOW_EMPTY:${PN}-test-main = "1"
ALLOW_EMPTY:${PN}-test-dep = "1"
RRECOMMENDS:${PN}-test-main = "${PN}-test-dep"
