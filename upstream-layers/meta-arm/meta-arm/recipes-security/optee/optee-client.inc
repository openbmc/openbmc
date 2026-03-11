SUMMARY = "OP-TEE Client API"
DESCRIPTION = "Open Portable Trusted Execution Environment - Normal World Client side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

inherit systemd update-rc.d cmake useradd

SRC_URI = " \
    git://github.com/OP-TEE/optee_client.git;branch=master;protocol=https \
    file://tee-supplicant.sh \
"

UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"

EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DCFG_USE_PKGCONFIG=ON \
"

# libts uses /dev/tee devices too. Add a common variable to allow configuring the same group.
TEE_GROUP_NAME ?= "tee"

EXTRA_OECMAKE += " -DCFG_ENABLE_SYSTEMD=On -DSYSTEMD_UNIT_DIR=${systemd_system_unitdir}/"
EXTRA_OECMAKE += " -DCFG_ENABLE_UDEV=On -DUDEV_UDEV_DIR=${nonarch_base_libdir}/udev/rules.d/"
EXTRA_OECMAKE += " -DCFG_TEE_GROUP=${TEE_GROUP_NAME} -DCFG_TEEPRIV_GROUP=teepriv"

EXTRA_OECMAKE:append:toolchain-clang = " -DCFG_WERROR=0"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -D -p -m0755 ${UNPACKDIR}/tee-supplicant.sh ${D}${sysconfdir}/init.d/tee-supplicant
        sed -i -e s:@sysconfdir@:${sysconfdir}:g \
               -e s:@sbindir@:${sbindir}:g \
               -e s:@supluser@:teesuppl:g \
               -e s:@suplgroup@:teesuppl:g \
                  ${D}${sysconfdir}/init.d/tee-supplicant
    fi
    install -o teesuppl -g teesuppl -m 0700 -d ${D}${localstatedir}/lib/tee
}

SYSTEMD_SERVICE:${PN} = "tee-supplicant@.service"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "tee-supplicant"
INITSCRIPT_PARAMS:${PN} = "start 10 1 2 3 4 5 . stop 90 0 6 ."

FILES:${PN} += "${nonarch_base_libdir}/udev/rules.d/"

# Users and groups:
# TEE_GROUP_NAME group to access /dev/tee*
# teepriv group to acess /dev/teepriv*, only tee-supplicant
# teesuppl user and group teesuppl to run tee-supplicant
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${TEE_GROUP_NAME}; --system teepriv; --system teesuppl"
USERADD_PARAM:${PN} = "--system -g teesuppl --groups teepriv --home-dir ${localstatedir}/lib/tee -M --shell /sbin/nologin teesuppl;"
