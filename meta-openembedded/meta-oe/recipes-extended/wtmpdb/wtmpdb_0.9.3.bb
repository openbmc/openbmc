SUMMARY = "Y2038 safe version of wtmp"
HOMEPAGE = "https://github.com/thkukuk/wtmpdb"
DESCRIPTION = "last reports the login and logout times of users and when the machine got rebooted."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=020090a00b69dd2af9ab82eb0003ea2c"
SECTION = "libs"

SRCREV = "8ef2677a13d19aee3a834500f9c8a4dac9d68ef7"

SRC_URI = "git://github.com/thkukuk/wtmpdb.git;branch=main;protocol=https \
           file://0001-remove-lto-to-fix-link-error-of-clang.patch \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd features_check

DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} sqlite3 "
REQUIRED_DISTRO_FEATURES = "pam"

SYSTEMD_SERVICE:${PN} = "wtmpdb-update-boot.service wtmpdb-rotate.service"

EXTRA_OEMESON = " -Dpamlibdir=${libdir}"

do_install:append () {
      if [ -d ${D}${prefix}/lib/systemd -a ${D}${prefix}/lib != `dirname ${D}${systemd_unitdir}` ]; then
          # Fix makefile hardcoded path assumptions for systemd (assumes $prefix)
          # without usrmerge distro feature enabled
          install -d `dirname ${D}${systemd_unitdir}`
          mv ${D}${prefix}/lib/systemd `dirname ${D}${systemd_unitdir}`
      fi
}

FILES:${PN} += " ${systemd_system_unitdir} "
FILES:${PN} += " ${libdir} "
FILES:${PN} += " ${nonarch_libdir}/tmpfiles.d/* "
