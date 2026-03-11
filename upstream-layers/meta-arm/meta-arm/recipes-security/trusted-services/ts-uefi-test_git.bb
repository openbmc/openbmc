DESCRIPTION = "Trusted Services uefi-test deployment for arm-linux. \
               Used for running service level tests from Linux user-space \
               on an Arm platform with real deployments of UEFI SMM services."

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "libts python3-protobuf-native"
RDEPENDS:${PN} += "libts arm-ffa-user"

OECMAKE_SOURCEPATH = "${S}/deployments/uefi-test/${TS_ENV}"

FILES:${PN} = "${bindir}/uefi-test"

do_install:append () {
    install -d ${D}${bindir}
    mv ${D}${TS_INSTALL}/bin/uefi-test ${D}${bindir}

    rm -r --one-file-system ${D}${TS_INSTALL}
}
