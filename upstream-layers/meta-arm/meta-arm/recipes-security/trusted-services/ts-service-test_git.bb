DESCRIPTION = "Trusted Services ts-service-test deployment for arm-linux. \
               Used for running service level tests from Linux user-space \
               on an Arm platform with real deployments of trusted services."

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "libts python3-protobuf-native"
RDEPENDS:${PN} += "libts"

OECMAKE_SOURCEPATH = "${S}/deployments/ts-service-test/${TS_ENV}"

FILES:${PN} = "${bindir}/ts-service-test"

do_install:append () {
    install -d ${D}${bindir}
    mv ${D}${TS_INSTALL}/bin/ts-service-test ${D}${bindir}

    rm -r --one-file-system ${D}${TS_INSTALL}
}
