DESCRIPTION = "Trusted Services ts-remote-test deployment for arm-linux."

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "libts"
RDEPENDS:${PN} += "libts"

OECMAKE_SOURCEPATH = "${S}/deployments/ts-remote-test/${TS_ENV}"

FILES:${PN} = "${bindir}/ts-remote-test"
