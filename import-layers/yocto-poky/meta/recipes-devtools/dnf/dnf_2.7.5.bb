SUMMARY = "Package manager forked from Yum, using libsolv as a dependency resolver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://PACKAGE-LICENSING;md5=bfc29916e11321be06924c4fb096fdcc \
                   "

SRC_URI = "git://github.com/rpm-software-management/dnf.git \
           file://0001-Corretly-install-tmpfiles.d-configuration.patch \
           file://0001-Do-not-hardcode-etc-and-systemd-unit-directories.patch \
           file://0005-Do-not-prepend-installroot-to-logdir.patch \
           file://0029-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0030-Run-python-scripts-using-env.patch \
           "

SRCREV = "564c44667c7014843fa6f1732621093114ec59b2"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake gettext bash-completion distutils3-base systemd

DEPENDS += "libdnf librepo libcomps python3-iniparse"

# manpages generation requires http://www.sphinx-doc.org/
EXTRA_OECMAKE = " -DWITH_MAN=0 -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}_class-target += " \
  python3-core \
  python3-codecs \
  python3-netclient \
  python3-email \
  python3-threading \
  python3-distutils \
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
  python3-gpg \
  "

RRECOMMENDS_${PN}_class-target += "gnupg"

# Create a symlink called 'dnf' as 'make install' does not do it, but
# .spec file in dnf source tree does (and then Fedora and dnf documentation
# says that dnf binary is plain 'dnf').
do_install_append() {
        lnr ${D}/${bindir}/dnf-3 ${D}/${bindir}/dnf
        lnr ${D}/${bindir}/dnf-automatic-3 ${D}/${bindir}/dnf-automatic
}

# Direct dnf-native to read rpm configuration from our sysroot, not the one it was compiled in
do_install_append_class-native() {
        create_wrapper ${D}/${bindir}/dnf \
                RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
                RPM_NO_CHROOT_FOR_SCRIPTS=1
}

SYSTEMD_SERVICE_${PN} = "dnf-makecache.service dnf-makecache.timer \
                         dnf-automatic.service dnf-automatic.timer \
                         dnf-automatic-download.service dnf-automatic-download.timer \
                         dnf-automatic-install.service dnf-automatic-install.timer \
                         dnf-automatic-notifyonly.service dnf-automatic-notifyonly.timer \
"
